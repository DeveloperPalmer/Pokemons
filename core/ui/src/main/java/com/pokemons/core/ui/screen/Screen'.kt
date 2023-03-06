package com.pokemons.core.ui.screen

import androidx.compose.runtime.Composable
import com.pokemons.mvi.BaseViewIntents
import com.pokemons.mvi.BaseViewModel
import com.pokemons.mvi.MviScreen
import com.pokemons.mvi.event.ViewEventConfig
import com.pokemons.mvi.event.ViewEventPresentation

@Composable
fun <VS : Any, VI : BaseViewIntents, VE : Any, VM : BaseViewModel<VS, VI, VE, *>> MviScreen(
  viewModel: VM,
  intents: VI,
  viewEventConfig: ViewEventConfig<VE> = baseViewEventConfig { ViewEventPresentation.None },
  content: @Composable (VS, VI) -> Unit
) {
  MviScreen(
    viewModel = viewModel,
    viewEventConfig = viewEventConfig,
    intents = intents,
    content = content
  )
}

fun <VE : Any> baseViewEventConfig(mapToPresentation: (VE) -> ViewEventPresentation): ViewEventConfig<VE> {
  return object : BaseViewEventConfig<VE>() {
    override fun mapToPresentation(event: VE): ViewEventPresentation {
      return mapToPresentation(event)
    }
  }
}

abstract class BaseViewEventConfig<VE : Any> : ViewEventConfig<VE> {
  abstract override fun mapToPresentation(event: VE): ViewEventPresentation
}
