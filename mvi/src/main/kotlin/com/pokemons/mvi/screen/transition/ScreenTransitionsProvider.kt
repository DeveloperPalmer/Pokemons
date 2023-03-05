package com.pokemons.mvi.screen.transition

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.compositionLocalOf
import com.pokemons.mvi.screen.Route
import ru.kode.mvi.screen.transition.ScreenTransition

val LocalScreenTransitionsProvider = compositionLocalOf<ScreenTransitionsProvider> {
  error("No BottomSheetContentInsets provided")
}

interface ScreenTransitionsProvider {
  fun registerTransition(route: Route, transition: ScreenTransition)
  @OptIn(ExperimentalAnimationApi::class)
  fun createTransitionAnimation(): AnimatedContentScope<Route>.() -> ContentTransform
}

fun createFadeTransition(
  transitionType: TransitionType = TransitionType.Fade,
): ScreenTransition {
  return ScreenTransition(transitionType)
}

fun createHorizontalTransition(
  transitionType: TransitionType = TransitionType.RightToLeft,
): ScreenTransition {
  return ScreenTransition(transitionType)
}

fun createVerticalTransition(
  transitionType: TransitionType = TransitionType.BottomToTop,
): ScreenTransition {
  return ScreenTransition(transitionType)
}
