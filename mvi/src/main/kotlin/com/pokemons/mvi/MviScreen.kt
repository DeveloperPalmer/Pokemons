package com.pokemons.mvi

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import com.pokemons.mvi.event.LocalViewEventsHostMediator
import com.pokemons.mvi.event.ViewEventConfig

@Composable
fun <VS : Any, VI : BaseViewIntents, VE : Any, VM : BaseViewModel<VS, VI, VE, *>> MviScreen(
  viewModel: VM,
  intents: VI,
  viewEventConfig: ViewEventConfig<VE>,
  content: @Composable (VS, VI) -> Unit
) {
  LifecycleEffect(viewModel = viewModel, intents = intents)
  val scope = rememberCoroutineScope()
  val state by viewModel.viewStateFlow.collectAsState(scope.coroutineContext)
  val viewEventsHostController = LocalViewEventsHostMediator.current
  LaunchedEffect(viewModel) {
    viewModel.viewEventsFlow
      .onEach { event ->
        viewEventsHostController.sendViewEvent(
          viewEventConfig.mapToPresentation(event)
        )
      }
      .collect()
  }

  content(state, intents)
}

@Composable
inline fun <reified VI : BaseViewIntents> rememberViewIntents(): VI {
  return remember { VI::class.java.newInstance() }
}

@Composable
private fun <VI : BaseViewIntents, VM : BaseViewModel<*, VI, *, *>> LifecycleEffect(viewModel: VM, intents: VI) {
  LaunchedEffect(Unit) {
    viewModel.onAttach(intents)
  }
  DisposableEffect(Unit) {
    onDispose {
      viewModel.onDetach()
    }
  }
}
