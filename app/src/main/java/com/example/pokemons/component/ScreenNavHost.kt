@file:OptIn(ExperimentalAnimationApi::class)

package com.example.pokemons.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastMap
import androidx.compose.ui.util.fastMaxBy
import com.sla.core.ui.screen.LocalNavigationRoute
import com.sla.feature.core.domain.entity.ScreenRotation
import com.sla.mvi.screen.ComposableScreen
import com.sla.mvi.screen.Route
import com.sla.mvi.screen.transition.LocalScreenTransitionsProvider
import com.sla.navigation.ScreenRegistry

@Composable
fun ScreenNavHost(
  screenRegistry: ScreenRegistry,
  saveableStateHolder: SaveableStateHolder,
  route: Route,
  onInitGraph: @DisallowComposableCalls suspend () -> Unit,
  modifier: Modifier = Modifier,
) {
  AppNavHost(
    modifier = modifier,
    screenRegistry = screenRegistry,
    saveableStateHolder = saveableStateHolder,
    route = route
  )
  LaunchedEffect(Unit) { onInitGraph() }
}

@Composable
fun AppNavHost(
  screenRegistry: ScreenRegistry,
  saveableStateHolder: SaveableStateHolder,
  route: Route,
  modifier: Modifier = Modifier,
  contentAlignment: Alignment = Alignment.Center,
) {
  AnimatedScreenTransition(
    modifier = modifier,
    contentAlignment = contentAlignment,
    destination = route,
  ) { animatedDestination ->
    CompositionLocalProvider(LocalNavigationRoute provides route) {
      screenRegistry.screen(animatedDestination)?.Content(saveableStateHolder)
    }
  }
}

@Composable
internal fun AnimatedScreenTransition(
  modifier: Modifier = Modifier,
  contentAlignment: Alignment,
  destination: Route,
  content: @Composable AnimatedVisibilityScope.(Route) -> Unit,
) {
  val spec = LocalScreenTransitionsProvider.current.createTransitionAnimation()
  updateTransition(
    targetState = destination,
    label = "transition_to_${destination.path}"
  )
    .AnimatedContent(
      modifier = modifier,
      contentAlignment = contentAlignment,
      transitionSpec = spec,
      contentKey = { it.path },
    ) {
      content(it)
    }
}

@Composable
private fun ComposableScreen.Content(saveableStateHolder: SaveableStateHolder) {
  saveableStateHolder.SaveableStateProvider(viewModel.id) {
    content(viewModel)
  }
}


/**
 * A copy of Scaffold which only supports BottomBar padding for content
 */
@Composable
internal fun ScreenHost(
  rotation: ScreenRotation,
  bottomBar: @Composable () -> Unit,
  content: @Composable (PaddingValues) -> Unit,
  modifier: Modifier = Modifier,
) {
  SubcomposeLayout(modifier) { constraints ->
    val looseConstraints = constraints.copy(minWidth = 0, minHeight = 0)
    val (layoutWidth, layoutHeight) = constraints.maxWidth to constraints.maxHeight

    layout(layoutWidth, layoutHeight) {
      val bottomBarPlaceables = subcompose(ScreenHostSlot.BottomBar) {
        bottomBar()
      }.fastMap { it.measure(looseConstraints) }

      val bottomBarHeight = bottomBarPlaceables.fastMaxBy { it.height }?.height ?: 0
      val bottomBarWidth = bottomBarPlaceables.fastMaxBy { it.width }?.width ?: 0

      val bodyContentPlaceables = subcompose(ScreenHostSlot.ScreenContent) {
        val padding = when (rotation) {
          ScreenRotation.PortraitUp,
          ScreenRotation.PortraitDown -> PaddingValues(bottom = bottomBarHeight.toDp())
          ScreenRotation.LandscapeRight -> PaddingValues(end = bottomBarWidth.toDp())
          ScreenRotation.LandscapeLeft -> PaddingValues(start = bottomBarWidth.toDp())
        }
        content(padding)
      }
        .fastMap {
          it.measure(looseConstraints.copy(maxHeight = layoutHeight.coerceAtLeast(looseConstraints.minHeight)))
        }

      bodyContentPlaceables.fastForEach { placeable ->
        placeable.place(0, 0)
      }
      val alignment = when (rotation) {
        ScreenRotation.PortraitUp,
        ScreenRotation.PortraitDown -> Alignment.BottomCenter
        ScreenRotation.LandscapeRight -> Alignment.CenterEnd
        ScreenRotation.LandscapeLeft -> Alignment.CenterStart
      }
      bottomBarPlaceables.fastForEach { placeable ->
        placeable.placeRelative(
          alignment.align(
            IntSize(bottomBarWidth, bottomBarHeight), IntSize(layoutWidth, layoutHeight), layoutDirection
          )
        )
      }
    }
  }
}

private enum class ScreenHostSlot {
  ScreenContent, BottomBar
}
