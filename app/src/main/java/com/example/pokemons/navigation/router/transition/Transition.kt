@file:OptIn(ExperimentalAnimationApi::class)

package com.example.pokemons.navigation.router.transition

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import com.pokemons.mvi.screen.Route
import com.pokemons.mvi.screen.transition.ScreenTransitionsProvider
import com.pokemons.mvi.screen.transition.TransitionType
import ru.kode.mvi.screen.transition.ScreenTransition

class AppScreenTransitionsProvider : ScreenTransitionsProvider {

  private val enterTransitions = mutableMapOf<String, (AnimatedContentScope<Route>.() -> EnterTransition?)?>()
  private val exitTransitions = mutableMapOf<String, (AnimatedContentScope<Route>.() -> ExitTransition?)?>()

  override fun registerTransition(route: Route, transition: ScreenTransition) {
    enterTransitions[route.path] = enterAnimation(transition.transitionType)
    exitTransitions[route.path] = exitAnimation(transition.transitionType)
  }

  override fun createTransitionAnimation(): AnimatedContentScope<Route>.() -> ContentTransform {
    return {
      ContentTransform(
        targetContentEnter = createEnterAnimation().invoke(this),
        initialContentExit = createExitAnimation().invoke(this),
        // some screens have different heights in non-initialized state.
        // sizeTransform is disabled to prevent transitions glitches in such cases
        sizeTransform = null
      )
    }
  }

  private fun createEnterAnimation(): AnimatedContentScope<Route>.() -> EnterTransition = {
    enterTransitions[targetState.path]?.invoke(this) ?: defaultEnterTransition.invoke(this)
  }

  private fun createExitAnimation(): AnimatedContentScope<Route>.() -> ExitTransition = {
    exitTransitions[initialState.path]?.invoke(this) ?: defaultExitTransition.invoke(this)
  }
}

internal val defaultEnterTransition: (AnimatedContentScope<Route>.() -> EnterTransition) =
  { fadeIn(animationSpec = tween(700)) }

internal val defaultExitTransition: (AnimatedContentScope<Route>.() -> ExitTransition) =
  { fadeOut(animationSpec = tween(700)) }

internal fun enterAnimation(
  type: TransitionType,
): (AnimatedContentScope<Route>.() -> EnterTransition) = {
  when (type) {
    TransitionType.LeftToRight -> slideIntoContainer(towards = AnimatedContentScope.SlideDirection.Right)
    TransitionType.RightToLeft -> slideIntoContainer(towards = AnimatedContentScope.SlideDirection.Left)
    TransitionType.TopToBottom -> slideIntoContainer(towards = AnimatedContentScope.SlideDirection.Down)
    TransitionType.BottomToTop -> slideIntoContainer(towards = AnimatedContentScope.SlideDirection.Up)
    TransitionType.Fade -> fadeIn()
    else -> EnterTransition.None
  }
}

internal fun exitAnimation(
  type: TransitionType,
): (AnimatedContentScope<Route>.() -> ExitTransition) = {
  when (type) {
    TransitionType.LeftToRight -> slideOutOfContainer(towards = AnimatedContentScope.SlideDirection.Left)
    TransitionType.RightToLeft -> slideOutOfContainer(towards = AnimatedContentScope.SlideDirection.Right)
    TransitionType.TopToBottom -> slideOutOfContainer(towards = AnimatedContentScope.SlideDirection.Up)
    TransitionType.BottomToTop -> slideOutOfContainer(towards = AnimatedContentScope.SlideDirection.Down)
    TransitionType.Fade -> fadeOut()
    else -> ExitTransition.None
  }
}


