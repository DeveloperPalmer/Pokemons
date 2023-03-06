@file:OptIn(ExperimentalMaterialApi::class, ExperimentalMaterialApi::class)

package com.pokemons.core.ui.scaffold

import androidx.compose.material.ExperimentalMaterialApi
import com.pokemons.core.ui.bottomsheet.BottomSheetState
import com.pokemons.mvi.screen.Route
import com.pokemons.mvi.screen.transition.TransitionType
import com.pokemons.mvi.screen.transition.createFadeTransition
import com.pokemons.mvi.screen.transition.createHorizontalTransition
import ru.kode.mvi.screen.transition.ScreenTransition

fun MainScaffoldController.push(state: MainScaffoldState) {
  requestStateChange { state }
}

fun MainScaffoldController.pushMain(
  route: Route,
  transition: ScreenTransition = createHorizontalTransition()
) {
  registerRouteTransition(route, transition)
  requestStateChange { state -> state.copy(route = route) }
}

@OptIn(
  ExperimentalMaterialApi::class)
fun MainScaffoldController.pushBottomSheet(
  route: Route,
  state: BottomSheetState,
  transition: ScreenTransition = createFadeTransition()
) {
  registerRouteTransition(route, transition)
  requestStateChange { scaffoldState ->
    when (scaffoldState.bottomSheet) {
      is MainScaffoldState.BottomSheet.Active -> scaffoldState.copy(
        bottomSheet = scaffoldState.bottomSheet.copy(route = route, state = state)
      )
      MainScaffoldState.BottomSheet.Hidden -> scaffoldState
    }
  }
}

fun MainScaffoldController.pushBottomSheet(
  route: Route,
  transition: ScreenTransition = createFadeTransition()
) {
  registerRouteTransition(route, transition)
  requestStateChange { state ->
    when (state.bottomSheet) {
      is MainScaffoldState.BottomSheet.Active -> state.copy(bottomSheet = state.bottomSheet.copy(route = route))
      MainScaffoldState.BottomSheet.Hidden -> state
    }
  }
}

fun MainScaffoldController.pushBottomSheet(bottomSheet: MainScaffoldState.BottomSheet) {
  requestStateChange { state -> state.copy(bottomSheet = bottomSheet) }
}

fun MainScaffoldController.pushBottomSheet(state: BottomSheetState) {
  requestStateChange { scaffoldState ->
    when (scaffoldState.bottomSheet) {
      is MainScaffoldState.BottomSheet.Active -> scaffoldState.copy(
        bottomSheet = scaffoldState.bottomSheet.copy(state = state)
      )
      MainScaffoldState.BottomSheet.Hidden -> scaffoldState
    }
  }
}

fun MainScaffoldController.pushBottomSheetState(transform: (BottomSheetState) -> BottomSheetState) {
  requestStateChange { scaffoldState ->
    when (scaffoldState.bottomSheet) {
      is MainScaffoldState.BottomSheet.Active -> scaffoldState.copy(
        bottomSheet = scaffoldState.bottomSheet.copy(state = transform(scaffoldState.bottomSheet.state))
      )
      MainScaffoldState.BottomSheet.Hidden -> scaffoldState
    }
  }
}

fun MainScaffoldController.pushBottomSheet(
  transform: (MainScaffoldState.BottomSheet) -> MainScaffoldState.BottomSheet
) {
  requestStateChange { scaffoldState ->
    scaffoldState.copy(bottomSheet = transform(scaffoldState.bottomSheet))
  }
}

fun MainScaffoldController.popMainTo(
  route: Route,
  transition: ScreenTransition = ScreenTransition(TransitionType.LeftToRight)
) {
  pushMain(route = route, transition = transition)
}

fun MainScaffoldController.popBottomSheetTo(
  route: Route,
  transition: ScreenTransition = ScreenTransition(TransitionType.Fade)
) {
  pushBottomSheet(route = route, transition = transition)
}

fun MainScaffoldController.popMain(
  transition: ScreenTransition = ScreenTransition(TransitionType.LeftToRight)
) {
  val route = findPreviousMainRoute()
  if (route != null) {
    pushMain(route = route, transition = transition)
  }
}
