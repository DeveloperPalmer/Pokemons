@file:OptIn(ExperimentalMaterialApi::class)

package com.pokemons.core.ui.scaffold

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Immutable
import com.pokemons.core.ui.bottomsheet.BottomSheetState
import com.pokemons.mvi.screen.Route

@Immutable
data class MainScaffoldState(
  val bottomSheet: BottomSheet = BottomSheet.Hidden,
  val route: Route,
  val id: StateId,
  val sequenceId: SequenceId,
) {

  @JvmInline
  value class StateId(val value: String)

  @Immutable
  sealed class BottomSheet {
    @Immutable
    data class Active constructor(
      val state: BottomSheetState,
      val route: Route,
      val visibility: Visibility = Visibility.Visible,
    ) : BottomSheet()

    object Hidden : BottomSheet()

    enum class Visibility {
      /**
       * Bottom sheet is visible to the user
       */
      Visible,

      /**
       * Bottom sheet is attached to the view, keeping it state, but is invisible to the user
       */
      Invisible
    }
  }
}

/**
 * If current state is [MainScaffoldState.BottomSheet.Active] then switches its visibility to
 * [MainScaffoldState.BottomSheet.Visibility.Invisible] and copies the rest of a sheet state as is.
 *
 * Otherwise does nothing.
 */
fun MainScaffoldState.BottomSheet.switchToInvisible(): MainScaffoldState.BottomSheet {
  return when (this) {
    is MainScaffoldState.BottomSheet.Active -> {
      this.copy(visibility = MainScaffoldState.BottomSheet.Visibility.Invisible)
    }
    is MainScaffoldState.BottomSheet.Hidden -> this
  }
}

/**
 * If current state is [MainScaffoldState.BottomSheet.Active] then switches its visibility to
 * [MainScaffoldState.BottomSheet.Visibility.Visible] and copies the rest of a sheet state as is.
 *
 * Otherwise does nothing.
 */
fun MainScaffoldState.BottomSheet.switchToVisible(): MainScaffoldState.BottomSheet {
  return when (this) {
    is MainScaffoldState.BottomSheet.Active -> {
      this.copy(visibility = MainScaffoldState.BottomSheet.Visibility.Visible)
    }
    is MainScaffoldState.BottomSheet.Hidden -> this
  }
}

/**
 * Produces short easily readable String for convenience when debugging MainScaffold and tests
 */
fun MainScaffoldState.toDebugString(): String {
  return "main: ${route.path}, ${bottomSheet.toDebugString()}, id: ${id.value}"
}

fun MainScaffoldState.BottomSheet.toDebugString(): String {
  return when (this) {
    is MainScaffoldState.BottomSheet.Active ->
      "bottomsheet: ${route.path}"
    is MainScaffoldState.BottomSheet.Hidden ->
      "bottomsheet: hidden"
  }
}
