package com.sla.core.ui.bottomsheet

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// using 'compositionLocalOf' instead of 'staticCompositionLocalOf', because these insets will change often
// and 'compositionLocalOf' provides a best performance for this case, otherwise dragging sheet
// will produce visible stuttering
val LocalBottomSheetContentInsets = compositionLocalOf<BottomSheetContentInsets> {
  error("No BottomSheetContentInsets provided")
}

@Immutable
data class BottomSheetContentInsets(
  /**
   * A height of the bottom sheet handle
   */
  val handleHeight: Dp,
  val sheetHeight: Int,
)


/**
 * Adds bottom padding equal to the bottom sheet height (if open)
 */
fun Modifier.bottomSheetPadding(): Modifier {
  return composed {
    val h = with(LocalDensity.current) { LocalBottomSheetContentInsets.current.sheetHeight.toDp() }
    padding(bottom = h)
  }
}

/**
 * If bottom sheet is open and it's bottom padding is larger than [paddingValues]'s bottom, then replaces bottom
 * padding in [paddingValues] with bottom sheet height else adds padding using [paddingValues] "as is"
 */
fun Modifier.bottomSheetPaddingMergedWith(paddingValues: PaddingValues): Modifier {
  return composed {
    val sheet = with(LocalDensity.current) { LocalBottomSheetContentInsets.current.sheetHeight.toDp() }
    val bottomPadding = paddingValues.calculateBottomPadding()
    if (bottomPadding < sheet) {
      padding(paddingValues).padding(bottom = sheet - bottomPadding)
    } else {
      padding(paddingValues)
    }
  }
}

/**
 * Adds top padding equal to bottom sheet handle height
 */
fun Modifier.bottomSheetHandlePadding(): Modifier {
  return composed {
    padding(top = LocalBottomSheetContentInsets.current.handleHeight)
  }
}

/**
 * Represents an anchor based bottom sheet state.
 *
 * Each anchor represents a bottom sheet position of certain height,
 * measured from bottom of the screen.
 *
 * See [Anchor] for description of several anchor types.
 */
data class BottomSheetState(
  val anchors: List<Anchor>,
  val currentAnchorId: Anchor.Id,
  /**
   * Whether to use default bottom sheet handle in layout.
   * Set to `false` if you want to provide a custom handle
   */
  val useDefaultHandle: Boolean = true,
  /**
   * Whether to close bottom sheet on dismiss (transition to MainScaffoldState.MainContent)
   */
  val closeOnDismiss: Boolean = true,
) {

  constructor(
    anchor: Anchor,
    useDefaultHandle: Boolean = true,
    closeOnDismiss: Boolean = true,
  ) : this(
    listOf(anchor),
    currentAnchorId = anchor.id,
    useDefaultHandle = useDefaultHandle,
    closeOnDismiss = closeOnDismiss,
  )

  init {
    require(anchors.isNotEmpty()) { "collapsed state must have at least one anchor" }
    require(anchors.any { it.id == currentAnchorId }) {
      "anchor set \"${anchors.joinToString { it.id.value }}\" does not contain anchor with id $currentAnchorId"
    }
    // having many intrinsic heights makes no sense — measure will be done prior to switch to state,
    // resulting height will vary anyway: what's there will be used
    require(anchors.count { it is Anchor.IntrinsicHeight } <= 1) {
      "at most one anchor of intrinsic height is supported"
    }
    val intrinsicAnchor = anchors.find { it is Anchor.IntrinsicHeight }
    require(intrinsicAnchor == null || currentAnchorId == intrinsicAnchor.id) {
      "when pushing a state with intrinsic anchor, it must be set as current"
    }
  }

  val currentAnchor get() = anchors.first { it.id == currentAnchorId }
}

sealed class Anchor {
  abstract val id: Id

  /**
   * An anchor of intrinsic height.
   * It can be used when you want bottom sheet height to be not fixed, but derived from the content head.
   *
   * This works in a very specific way:
   * 1. Pushed bottom sheet state must have anchor with [IntrinsicHeight] set as current
   * 2. At the time of push bottom sheet content must have some fixed height constraints.
   *    For example having a `LazyColumn` inside will not work and is not supported, it will
   *    throw an exception
   * 3. After bottom sheet state with this anchor is pushed, content will be measured and
   *    in case bottom sheet will be swiped by the user to some other state, then a **new**
   *    bottom sheet state will be automatically pushed which won't contain [IntrinsicHeight]
   *    anchor anymore, and will contain a [Fixed] anchor with the same anchor id
   *
   * If the above mechanism does not suite your needs, you'll need to provide your own internal sheet content
   * measurements and implement some custom `pushBottomSheetState` calls to suit your situation.
   */
  object IntrinsicHeight : Anchor() {
    override val id = Id("intrinsic_height")
  }

  /**
   * An anchor with the specified bottom sheet height
   *
   * @param id a unique id of an anchor
   * @param min minimum height of a sheet at this anchor
   * @param max maximum height of a sheet at this anchor. By default it is unbounded which means sheet layout
   * can extend beyond screen
   */
  data class Fixed(override val id: Id, val min: Dp, val max: Dp? = null) : Anchor()

  /**
   * An anchor representing a fully expanded bottom sheet
   */
  object Expanded : Anchor() {
    override val id = Id("expanded")
  }

  @JvmInline
  value class Id(val value: String)

  companion object {
    private var NEXT_ID = 0
    fun newId() = Id(NEXT_ID++.toString())
  }
}

val BOTTOM_SHEET_WIDTH_LANDSCAPE = 308.dp
val BOTTOM_SHEET_SIDE_PADDING_LANDSCAPE = 16.dp
