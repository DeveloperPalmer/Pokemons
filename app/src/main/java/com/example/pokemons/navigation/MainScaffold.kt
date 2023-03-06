@file:OptIn(
  ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class,
  ExperimentalComposeUiApi::class
)

package com.example.pokemons.navigation

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import com.example.pokemons.navigation.router.AppMainScaffoldController
import com.pokemons.core.ui.LocalScreenConfiguration
import com.pokemons.core.ui.bottomsheet.*
import com.pokemons.core.ui.bottomsheet.Anchor
import com.pokemons.core.ui.bottomsheet.BottomSheetState
import com.pokemons.core.ui.scaffold.LocalMainScaffoldController
import com.pokemons.core.ui.scaffold.MainScaffoldState
import com.pokemons.core.ui.scaffold.MainScaffoldState.BottomSheet
import com.pokemons.core.ui.scaffold.MainScaffoldState.StateId
import com.pokemons.core.ui.scaffold.MainScaffoldStateProvider
import com.pokemons.feature.core.domain.entity.ScreenOrientation
import com.pokemons.feature.core.domain.entity.ScreenRotation
import com.pokemons.feature.core.domain.randomUuid
import com.pokemons.mvi.event.LocalViewEventsHostMediator
import com.pokemons.mvi.event.ViewEventsHost
import com.pokemons.mvi.event.ViewEventsHostMediator
import com.pokemons.mvi.screen.Route
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.scan
import timber.log.Timber
import kotlin.math.roundToInt
import com.pokemons.core.ui.theme.AppTheme

/**
 * A main screen scaffolding which consists of two parts: main content and bottom sheet on top of it.
 *
 * Inside [content] you can access bottom sheet's insets (height) using [LocalBottomSheetContentInsets].
 * Alternatively you can use [Modifier.bottomSheetPadding] or [Modifier.bottomSheetPaddingMergedWith] modifiers.
 *
 * Inside [sheetContent] you can access a height of bottom sheet toggle using [LocalBottomSheetContentInsets].
 * Alternatively you can use [Modifier.bottomSheetHandlePadding] modifier.
 *
 * Use [BottomSheetRouter] to navigate to various destinations inside the bottom sheet.
 *
 * Also a [LocalMainScaffoldController] is provided to access bottom sheet state inside composables
 */
@Composable
internal fun MainScaffold(
  controller: AppMainScaffoldController,
  content: @Composable (Route) -> Unit,
  sheetContent: @Composable (Route) -> Unit,
) {
  val viewEventsHostMediator = remember { ViewEventsHostMediator() }
  // Note that this states represents **intent** and is rendered "in advance" — so that animation from current state
  // to this new state can happen. Only after animated transition is finished, it gets applied:
  // see MainScaffoldStateApplyEffect and UpdateSheetStateOnSettleEffect
  val requestedState by remember {
    controller.stateChangeRequests()
  }.collectAsState(controller.latestState)

  CompositionLocalProvider(
    LocalMainScaffoldController provides controller,
    LocalViewEventsHostMediator provides viewEventsHostMediator,
  ) {
    val contentInsetsState = remember { mutableStateOf(BottomSheetContentInsets(0.dp, 0)) }

    MainScaffoldLayout(
      modifier = Modifier.semantics { testTagsAsResourceId = true },
      state = requestedState,
      content = {
        CompositionLocalProvider(
          LocalBottomSheetContentInsets provides contentInsetsState.value,
        ) {
          content(requestedState.route)
        }
      },
      sheetContent = { layoutState ->
        val previousStateAnchorId =
          rememberPreviousStateAnchorId(requestedState, layoutState, controller)

        val density = LocalDensity.current
        val bottomSheetState = requestedState.bottomSheet
        val contentState = remember(requestedState, layoutState) {
          buildSwipeableState(
            bottomSheetState,
            previousStateAnchorId,
            layoutState,
            density
          )
        }
        BottomSheetContent(contentState = contentState, contentInsetsState) {
          if (bottomSheetState is BottomSheet.Active) {
            sheetContent(bottomSheetState.route)
          }
        }
        if (contentState != null) {
          UpdateSheetStateOnSettleEffect(state = contentState, holder = controller)
        }
        MainScaffoldStateApplyEffect(requestedState, contentState, controller, contentInsetsState)
      }
    )
  }
}

@Composable
private fun rememberPreviousStateAnchorId(
  state: MainScaffoldState,
  layoutState: MainScaffoldLayoutState,
  holder: MainScaffoldStateProvider,
): Anchor.Id {
  return remember(state, layoutState) {
    when (val sheet = holder.readMainScaffoldState().bottomSheet) {
      is BottomSheet.Active -> {
        when (sheet.visibility) {
          BottomSheet.Visibility.Visible -> sheet.state.currentAnchorId
          BottomSheet.Visibility.Invisible -> AnchorHidden.id
        }
      }
      is BottomSheet.Hidden -> {
        // when sheet is set to IntrinsicHeight and it recomposes multiple times while staying on intrinsic height
        // (for example using animateContentSize()), then layoutState will be recomputed many times while
        // state with intrinsic size is still not applied and previousAnchor will be kept at Hidden.
        // So in this case substitute IntrinsicHeight as previousAnchor, otherwise this can lead
        // to multiple animation jumps especially if previously current anchor
        // before IntrinsicHeight was AnchorHidden
        // (check RoutesMain + switching between transport types if you change this!)
        if (layoutState.hasCalculatedIntrinsicHeight) Anchor.IntrinsicHeight.id else AnchorHidden.id
      }
    }
  }
}

@Composable
private fun MainScaffoldStateApplyEffect(
  newState: MainScaffoldState,
  contentState: BottomSheetContentState?,
  holder: MainScaffoldStateProvider,
  contentInsetsState: MutableState<BottomSheetContentInsets>
) {
  val targetAnchor = when (val bottomSheet = newState.bottomSheet) {
    is BottomSheet.Active -> {
      val value = when (bottomSheet.visibility) {
        BottomSheet.Visibility.Visible -> bottomSheet.state.currentAnchorId.value
        BottomSheet.Visibility.Invisible -> AnchorHidden.id.value
      }
      value
    }
    is BottomSheet.Hidden -> AnchorHidden.id.value
  }
  val sheetInsets = calculateSheetInsets(contentState)
  // Ignore animation related exceptions of swipeable animation
  // These are caused by SwipeableState concurrency issues
  // and should not lead to incorrect state application or runtime crashes
  @Suppress("TooGenericExceptionCaught")
  LaunchedEffect(newState, contentState) {
    try {
      contentState?.swipeableState?.animateTo(targetAnchor)
      // sheetHeight must be updated, because swipeableState is at the new anchor now
      contentInsetsState.value =
        sheetInsets.copy(sheetHeight = contentState?.calculateSheetHeight() ?: 0)
      holder.accept(newState)
    } catch (ex: Throwable) {
      if (ex !is CancellationException) holder.accept(newState)
    }
  }
}

private fun buildSwipeableState(
  bottomSheet: BottomSheet,
  previousAnchorId: Anchor.Id?,
  layoutState: MainScaffoldLayoutState,
  density: Density,
): BottomSheetContentState? {
  return when (bottomSheet) {
    is BottomSheet.Active -> {
      val swipeableAnchors = bottomSheet.state.anchors.toSwipeableAnchors(
        density = density,
        layoutState = layoutState,
        currentAnchor = bottomSheet.state.currentAnchor
      )
      BottomSheetContentState(
        swipeableState = SwipeableState(
          initialValue = previousAnchorId?.value?.takeIf { swipeableAnchors?.containsValue(it) == true }
            ?: bottomSheet.state.currentAnchorId.value,
          // See NOTE_BOUNDARY_PEEK_ANCHORS
          confirmStateChange = { id ->
            // accepting all changes except when "hidden" state is requested and targetStateAfterDismiss
            // is not MainContent which means that bottom sheet cannot be dismissed and hidden
            (id != AnchorHidden.id.value || bottomSheet.state.closeOnDismiss)
          },
        ),
        // See NOTE_BOTTOM_SHEET_INTRINSIC_SIZE_AND_SWIPEABLE
        // See NOTE_BOTTOM_SHEET_LAYOUT_RECOMPOSITION
        swipeableAnchors = swipeableAnchors,
        layoutState = layoutState,
        closeOnDismiss = bottomSheet.state.closeOnDismiss,
        useDefaultHandle = bottomSheet.state.useDefaultHandle,
        isInvisible = bottomSheet.visibility == BottomSheet.Visibility.Invisible
      )
    }
    is BottomSheet.Hidden -> null
  }
}

@Composable
private fun BottomSheetContent(
  contentState: BottomSheetContentState?,
  contentInsetsState: MutableState<BottomSheetContentInsets>,
  sheetContent: @Composable () -> Unit,
) {
  val screenConfiguration = LocalScreenConfiguration.current

  val corner = animateCornersAsState(
    screenOrientation = screenConfiguration.orientation,
    useRoundCorners = contentState?.useRoundCorners == true
  )
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .let { if (contentState != null) it.sheetContent(contentState) else it }
      .background(
        color = AppTheme.colors.backgroundMain,
        shape = RoundedCornerShape(topStart = corner.value, topEnd = corner.value)
      )
  ) {
    val contentInsets = calculateSheetInsets(contentState)
    // not updating insets if swipeableAnchors are not calculated yet, this happens with intrinsic anchors.
    // otherwise sheet height would be calculated as screenHeight which leads to visual jumps (of map controls)
    if (contentState?.swipeableAnchors != null) {
      contentInsetsState.value = contentInsets
    }
    if (contentState?.useDefaultHandle == true) {
      DefaultSheetHandle(
        handleHeight = contentInsets.handleHeight,
        visible = contentState.isHandleVisible
      )
    }
    CompositionLocalProvider(
      LocalBottomSheetContentInsets provides contentInsetsState.value,
    ) {
      sheetContent()
    }
  }
}

@Composable
private fun calculateSheetInsets(contentState: BottomSheetContentState?): BottomSheetContentInsets {
  return BottomSheetContentInsets(
    handleHeight = contentState?.calculateSheetHandleHeight() ?: 0.dp,
    sheetHeight = contentState?.calculateSheetHeight() ?: 0
  )
}

private fun BottomSheetContentState.calculateSheetHeight(): Int {
  return this.layoutState.maxHeight - this.swipeableState.offset.value.roundToInt()
}

@Composable
private fun BottomSheetContentState.calculateSheetHandleHeight(): Dp {
  val screenConfiguration = LocalScreenConfiguration.current
  return if (this.useDefaultHandle) {
    defaultHandleHeight(screenOrientation = screenConfiguration.orientation)
  } else 0.dp
}

@Composable
private fun animateCornersAsState(
  screenOrientation: ScreenOrientation,
  useRoundCorners: Boolean
): State<Dp> {
  return animateDpAsState(
    targetValue = when (screenOrientation) {
      ScreenOrientation.Portrait -> 16.dp
      ScreenOrientation.Landscape -> {
        if (!useRoundCorners)
          0.dp else 16.dp
      }
    }
  )
}

@Composable
private fun defaultHandleHeight(screenOrientation: ScreenOrientation): Dp {
  return with(LocalDensity.current) {
    when (screenOrientation) {
      ScreenOrientation.Portrait -> 16.dp
      ScreenOrientation.Landscape -> WindowInsets.statusBars.getTop(this).toDp()
    }
  }
}

@Composable
private fun DefaultSheetHandle(handleHeight: Dp, visible: Boolean) {
  val screenConfiguration = LocalScreenConfiguration.current
  when (screenConfiguration.orientation) {
    ScreenOrientation.Portrait -> {
      BottomSheetHandle(modifier = Modifier.height(handleHeight))
    }
    ScreenOrientation.Landscape -> {
      if (!visible) {
        Spacer(modifier = Modifier.height(handleHeight))
      } else {
        BottomSheetHandle(modifier = Modifier.height(handleHeight))
      }
    }
  }
}

private fun Modifier.sheetContent(contentState: BottomSheetContentState): Modifier {

  return if (contentState.swipeableAnchors != null) {
    this
      .offset { IntOffset(x = 0, y = contentState.swipeableState.offset.value.roundToInt()) }
      .nestedScroll(contentState.swipeableState.PreUpPostDownNestedScrollConnection)
      .swipeable(
        state = contentState.swipeableState,
        anchors = contentState.swipeableAnchors,
        orientation = Orientation.Vertical,
        resistance = SwipeableDefaults.resistanceConfig(
          factorAtMin = 0f,
          anchors = contentState.swipeableAnchors.keys
        ),
        enabled = !contentState.swipeableState.isAnimationRunning,
      )
  } else this.alpha(0f)
}

@Stable
private data class BottomSheetContentState(
  val swipeableState: SwipeableState<String>,
  val swipeableAnchors: Map<Float, String>?,
  val layoutState: MainScaffoldLayoutState,
  val closeOnDismiss: Boolean,
  val useDefaultHandle: Boolean,
  val isInvisible: Boolean,
) {
  val isHandleVisible get() = useDefaultHandle && swipeableState.currentValue != Anchor.Expanded.id.value
  val useRoundCorners get() = swipeableState.currentValue != Anchor.Expanded.id.value
}

@Composable
private fun UpdateSheetStateOnSettleEffect(
  state: BottomSheetContentState,
  holder: MainScaffoldStateProvider,
) {
  val density = LocalDensity.current
  SwipeSettleFinishEffect(state.swipeableState) {
    // invisible is not a user generated state, no adjustments are needed
    if (state.isInvisible) return@SwipeSettleFinishEffect
    val latestState = holder.readMainScaffoldState()
    // State with this anchor is already applied, no adjustments are needed
    if (state.swipeableState.currentValue == latestState.bottomSheet.currentAnchor.value) return@SwipeSettleFinishEffect

    val scaffoldState = latestState
      .adjustToNewAnchor(state.swipeableState.currentValue) {
        if (state.swipeableAnchors != null && state.swipeableState.currentValue != AnchorHidden.id.value)
          it.adjustedIntrinsicsAfterSwipeSettle(
            density = density,
            swipeableState = state.swipeableState,
            swipeableAnchors = state.swipeableAnchors,
            layoutState = state.layoutState
          )
        else it
      }
    holder.accept(scaffoldState)
  }
}

private fun MainScaffoldState.adjustToNewAnchor(
  anchorId: String,
  transform: (BottomSheetState) -> BottomSheetState
): MainScaffoldState {
  return if (anchorId == AnchorHidden.id.value) {
    copy(bottomSheet = BottomSheet.Hidden, id = StateId(randomUuid()))
  } else when (val sheet = bottomSheet) {
    is BottomSheet.Active -> copy(
      bottomSheet = sheet.copy(state = transform(sheet.state)),
      id = StateId(randomUuid())
    )
    is BottomSheet.Hidden -> copy(id = StateId(randomUuid()))
  }
}

private const val CONTENT_SLOT_ID = 0
private const val SHEET_SLOT_ID = 1

@Suppress("ComplexMethod") // layout logic which is best kept in one place for easier review
@Composable
private fun MainScaffoldLayout(
  state: MainScaffoldState,
  content: @Composable () -> Unit,
  sheetContent: @Composable (layoutState: MainScaffoldLayoutState) -> Unit,
  modifier: Modifier = Modifier,
) {
  val density = LocalDensity.current
  val statusBarInsets = WindowInsets.statusBars
  val navigationBarInsets = WindowInsets.navigationBars
  val cutoutInsets = WindowInsets.displayCutout
  val (screenOrientation, screenRotation) = LocalScreenConfiguration.current

  // NOTE_BOTTOM_SHEET_LAYOUT_RECOMPOSITION
  // When using IntrinsicSize anchors layout **has to** recompose 2 times:
  // 1. Measure sheetContent() size with infinity-constraint, letting it be as tall as it wants.
  //    In this stage sheetContent() won't be able to build swipeable anchors, because height is not known
  // 2. Recompose and measure sheetContent() again this time passing it measured intrinsic height.
  //    Now sheetContent() will be able to build anchors and create a swipeable modifier
  val anchor = (state.bottomSheet as? BottomSheet.Active)?.state?.currentAnchor
  var sheetIntrinsicHeight by remember(anchor) { mutableStateOf<Int?>(null) }

  SubcomposeLayout(modifier) { constraints ->
    val maxHeight = constraints.maxHeight
    val layoutState by derivedStateOf {
      val topPadding = when (screenOrientation) {
        ScreenOrientation.Portrait -> statusBarInsets.getTop(density) + 16.dp.roundToPx()
        ScreenOrientation.Landscape -> 0
      }
      MainScaffoldLayoutState(
        maxHeight = maxHeight,
        topPadding = topPadding,
        bottomSheetIntrinsicHeight = sheetIntrinsicHeight
      )
    }
    val (sheetMinHeight, sheetMaxHeight) = anchor?.resolveSheetHeight(
      density,
      layoutState
    )
      ?: (0 to 0) // TODO @dz remove elvis, move val (sheetMinHeight, sheetMaxHeight) in if/else below
    val sheetPlaceables = subcompose(SHEET_SLOT_ID) {
      sheetContent(layoutState)
    }.map {
      val maxWidth = when (screenOrientation) {
        ScreenOrientation.Portrait -> constraints.maxWidth
        ScreenOrientation.Landscape -> BOTTOM_SHEET_WIDTH_LANDSCAPE.roundToPx()
      }
      if (anchor == Anchor.IntrinsicHeight) {
        it.measure(
          constraints.copy(
            maxWidth = maxWidth,
            maxHeight = (maxHeight - layoutState.topPadding).coerceAtLeast(constraints.minHeight)
          )
        )
      } else {
        it.measure(
          constraints.copy(
            maxWidth = maxWidth,
            minHeight = sheetMinHeight,
            maxHeight = sheetMaxHeight ?: maxOf(sheetMinHeight, maxHeight - layoutState.topPadding),
          )
        )
      }
    }

    if (anchor == Anchor.IntrinsicHeight) {
      sheetIntrinsicHeight = sheetPlaceables.maxOfOrNull { it.height } ?: 0
    }

    val contentPlaceables = subcompose(CONTENT_SLOT_ID) {
      content()
    }.map { it.measure(constraints) }

    val width = (sheetPlaceables + contentPlaceables)
      .maxOfOrNull { it.width }
      // NOTE_SCAFFOLD_LAYOUT_CONSTRAINTS Respect constraints here.
      // Required for example because sheetPlaceables can sometimes hardcode sheet width and end up not respecting
      // our boundaries as a parent, and this can mess up rendering: for example in Picture in Picture mode
      ?.coerceAtMost(constraints.maxWidth)
      ?: 0
    val height = (sheetPlaceables + contentPlaceables)
      .maxOfOrNull { it.height }
      // See NOTE_SCAFFOLD_LAYOUT_CONSTRAINTS
      ?.coerceAtMost(constraints.maxHeight)
      ?: 0

    layout(width, height) {
      contentPlaceables.forEach { it.placeRelative(0, 0) }
      sheetPlaceables.forEach {
        when (screenRotation) {
          ScreenRotation.PortraitUp,
          ScreenRotation.PortraitDown -> {
            it.place(0, 0)
          }
          ScreenRotation.LandscapeRight -> {
            it.place(
              width -
                  navigationBarInsets.getRight(density, layoutDirection) -
                  cutoutInsets.getRight(density, layoutDirection) -
                  it.width -
                  BOTTOM_SHEET_SIDE_PADDING_LANDSCAPE.roundToPx(),
              0
            )
          }
          ScreenRotation.LandscapeLeft -> {
            it.place(
              navigationBarInsets.getLeft(density, layoutDirection) + cutoutInsets.getLeft(
                density,
                layoutDirection
              ) +
                  BOTTOM_SHEET_SIDE_PADDING_LANDSCAPE.roundToPx(),
              0
            )
          }
        }
      }
    }
  }
}

private data class MainScaffoldLayoutState(
  val maxHeight: Int,
  val topPadding: Int,
  // See NOTE_BOTTOM_SHEET_LAYOUT_RECOMPOSITION
  val bottomSheetIntrinsicHeight: Int?,
) {
  val hasCalculatedIntrinsicHeight get() = bottomSheetIntrinsicHeight != null && bottomSheetIntrinsicHeight > 0
}

private val BottomSheet.currentAnchor: Anchor.Id
  get() = when (this) {
    is BottomSheet.Active -> state.currentAnchorId
    BottomSheet.Hidden -> AnchorHidden.id
  }

private fun BottomSheetState.adjustedIntrinsicsAfterSwipeSettle(
  density: Density,
  swipeableState: SwipeableState<String>,
  swipeableAnchors: Map<Float, String>,
  layoutState: MainScaffoldLayoutState,
): BottomSheetState {
  // NOTE_BOTTOM_SHEET_INTRINSIC_SIZE_AND_SWIPEABLE

  // [swipe_from_intrinsic_to_another_anchor]
  // After bottom sheet was at IntrinsicSize anchor and then got swiped to another anchor,
  // we **must** replace an anchor which was IntrinsicSize to be a Fixed-size anchor.
  // This is required because Swipeable **needs** to know anchor sizes in advance:
  // If it were not so: imagine if user is currently in an Expanded state and wants to
  // swipe to IntrinsicSize anchor. How would that work? We need to pass an exact height in *pixels* to
  // the Swipeable modifier, but we are currently in Expanded state and didn't measure IntrinsicSize yet,
  // so we can't properly build the swipeable state for this bottom sheet state
  //
  // [swipe_from_another_anchor_to_intrinsic]
  // Another case is when user switches from non-intrinsic (Expanded or Fixed) state to IntrinsicHeight **after**
  // it was resolved. In this case intrinsic anchor is actually a "fixed" one, so if layout will keep changing after
  // switch, then it won't behave as intrinsic anymore, it won't re-measure. To fix this, if we detect
  // the switch from non-intrinsic to the **resolved** Intrinsic, we replace that resolved with unresolved.
  //
  // Use case:
  //
  // 1. Switch from intrinsic to expanded
  // 2. Switch from expanded back to intrinsic
  // 3. Change composable layout height to a smaller one (it should resize sheet!)
  //
  // TODO this case is currently not implemented (see TODO below). Issue:
  //   https://convexitydmcc.atlassian.net/browse/MAPS-2085

  val targetAnchorId = Anchor.Id(swipeableState.currentValue)
  if (anchors.none { it.id == targetAnchorId }) {
    // According to Crashlytics, some users get in this state, but it's unclear why and where this happens.
    // For now "fixing" this by returning the unmodified state.
    // Another option would be to return "this.copy(targetAnchorId = targetAnchorId)", but it's unclear which one
    // is best, both can lead to further problems.
    // It's better to completely eliminate this problem after reproducing it!
    Timber.e("internal error! anchors list $anchors does not contain targetAnchor=$targetAnchorId")
    return this
  }
  return if (targetAnchorId != Anchor.IntrinsicHeight.id && this.anchors.any { it.id == Anchor.IntrinsicHeight.id }) {
    // this is [swipe_from_intrinsic_to_another_anchor], see above
    this.copy(
      anchors = this.anchors.map { a ->
        if (a.id == Anchor.IntrinsicHeight.id) {
          a.resolve(density, swipeableAnchors, layoutState)
        } else a
      },
      currentAnchorId = targetAnchorId
    )
  } else if (this.anchors.firstOrNull { it.id == targetAnchorId }
      ?.isResolvedIntrinsicAnchor() == true
  ) {
    // this is [swipe_from_another_anchor_to_intrinsic], see above
    this.copy(
      anchors = this.anchors.map { a ->
        if (a.isResolvedIntrinsicAnchor()) {
          a.unresolve()
        } else a
      },
      currentAnchorId = targetAnchorId
    )
  } else {
    this.copy(currentAnchorId = targetAnchorId)
  }
}

// If changing, remember to keep two functions in sync:
//   anchor.resolve().isResolvedIntrinsicAnchor() must be `true`
private fun Anchor.isResolvedIntrinsicAnchor(): Boolean {
  return this is Anchor.Fixed && this.id == Anchor.IntrinsicHeight.id
}

// If changing, remember to keep two functions in sync:
//   anchor.resolve().isResolvedIntrinsicAnchor() must be `true`
private fun Anchor.resolve(
  density: Density,
  swipeableAnchors: Map<Float, String>,
  layoutState: MainScaffoldLayoutState,
): Anchor {
  val height = with(density) {
    (layoutState.maxHeight - swipeableAnchors.entries.first { it.value == this@resolve.id.value }.key).toDp()
  }
  return Anchor.Fixed(id = Anchor.IntrinsicHeight.id, min = height)
}

private fun Anchor.unresolve(): Anchor {
  // TODO: return Anchor.IntrinsicHeight
  //   unresolving should simply switch back to IntrinsicHeight, but currently this causes whole sheet to
  //   dissappear + blink for a moment, which is caused by intrinsicAnchor height calculation state being
  //   reset to `null` and remeasured. Perhaps the solution is keep the oldest calculated height around cached
  //   for a bit longer and reused during subcompose
  //   Issue: https://convexitydmcc.atlassian.net/browse/MAPS-2085
  return this
}

@Composable
private fun SwipeSettleFinishEffect(
  swipeableState: SwipeableState<String>,
  onSettle: () -> Unit,
) {
  LaunchedEffect(swipeableState.targetValue) {
    snapshotFlow { swipeableState.isAnimationRunning }
      .scan(swipeableState.isAnimationRunning) { prevIsAnimating, isAnimating ->
        if (prevIsAnimating && !isAnimating) {
          onSettle()
        }
        isAnimating
      }
      .collect()
  }
}

private fun List<Anchor>.toSwipeableAnchors(
  density: Density,
  layoutState: MainScaffoldLayoutState,
  currentAnchor: Anchor?,
): Map<Float, String>? {
  // intrinsic height is measured on the next frame, so it could be null for some time, and also occasionally it
  // gets measured as 0 even though content is there, so wait until actual measurement
  return if (currentAnchor == null || (
        currentAnchor is Anchor.IntrinsicHeight && !layoutState.hasCalculatedIntrinsicHeight
        )
  ) {
    null
  } else {
    val anchors = this
      .distinct()
      .map { it.toSwipeableAnchor(density, layoutState) }
      .also { anchors ->
        check(anchors.distinctBy { it.first }.size == anchors.size) {
          // this will not work well with swipeable
          "several anchors had the same resolved height: " +
              anchors.joinToString { (height, id) ->
                "$id => height=$height"
              }
        }
      }
      .toMap(mutableMapOf())

    // NOTE_BOUNDARY_PEEK_ANCHORS
    // Explicitly add boundary anchors in order to allow dragging interactions without an actual switch.
    // Valid transitions are described by SwipeableState.confirmStateChange parameter
    val boundaryAnchors = arrayOf(AnchorHidden)
    boundaryAnchors.forEach { anchor ->
      val (height, id) = anchor.toSwipeableAnchor(density, layoutState)
      // Add boundary anchor only if no anchor with the same height is provided by the client.
      // Otherwise adding boundary would replace the client anchor and mess everything up
      if (!anchors.containsKey(height)) {
        anchors[height] = id
      }
    }
    anchors
  }
}

private fun Anchor.toSwipeableAnchor(
  density: Density,
  layoutState: MainScaffoldLayoutState
): Pair<Float, String> {
  return (
      layoutState.maxHeight -
          this.resolveSheetHeight(density, layoutState).first.toFloat()
      ) to this.id.value
}

private fun Anchor.resolveSheetHeight(
  density: Density,
  layoutState: MainScaffoldLayoutState,
): Pair<Int, Int?> {
  return when (this) {
    is Anchor.Fixed -> with(density) {
      this@resolveSheetHeight.min.roundToPx() to this@resolveSheetHeight.max?.roundToPx()
    }
    is Anchor.IntrinsicHeight -> {
      (layoutState.bottomSheetIntrinsicHeight ?: 0) to (layoutState.bottomSheetIntrinsicHeight ?: 0)
    }
    is Anchor.Expanded -> {
      (layoutState.maxHeight - layoutState.topPadding) to (layoutState.maxHeight - layoutState.topPadding)
    }
  }
}

private val AnchorHidden = Anchor.Fixed(Anchor.Id("hidden_internal"), min = 0.dp, max = 0.dp)

internal val <T> SwipeableState<T>.PreUpPostDownNestedScrollConnection: NestedScrollConnection
  get() = object : NestedScrollConnection {
    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
      val delta = available.toFloat()
      return if (delta < 0 && source == NestedScrollSource.Drag) {
        performDrag(delta).toOffset()
      } else {
        Offset.Zero
      }
    }

    override fun onPostScroll(
      consumed: Offset,
      available: Offset,
      source: NestedScrollSource
    ): Offset {
      return if (source == NestedScrollSource.Drag) {
        performDrag(available.toFloat()).toOffset()
      } else {
        Offset.Zero
      }
    }

    override suspend fun onPreFling(available: Velocity): Velocity {
      val toFling = Offset(available.x, available.y).toFloat()
      // TODO @dz in the original file from Compose, maxBound was taken from SwipeableState.
      //   Unfortunately it's internal and can't be accessed at all. Usually it seems to be
      //   equal to the bottomSheetHeight, so I've picked some random value for this hack
      //   and it works
      return if (toFling < 0 && offset.value > 300) {
        performFling(velocity = toFling)
        // since we go to the anchor with tween settling, consume all for the best UX
        available
      } else {
        Velocity.Zero
      }
    }

    override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
      performFling(velocity = Offset(available.x, available.y).toFloat())
      return available
    }

    private fun Float.toOffset(): Offset = Offset(0f, this)

    private fun Offset.toFloat(): Float = this.y
  }


@Composable
fun BottomSheetHandle(modifier: Modifier = Modifier) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(16.dp),
    contentAlignment = Alignment.Center
  ) {
    Spacer(
      modifier = Modifier
        .width(40.dp)
        .height(4.dp)
        .background(
          color = AppTheme.colors.textPrimary.copy(alpha = 0.15f),
          shape = RoundedCornerShape(4.dp)
        ),
    )
  }
}
