package com.sla.core.ui.screen

import androidx.compose.runtime.Composable
import com.sla.mvi.BaseViewIntents
import com.sla.mvi.BaseViewModel
import com.sla.mvi.MviScreen
import com.sla.mvi.event.ViewEventConfig
import com.sla.mvi.event.ViewEventPresentation

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
