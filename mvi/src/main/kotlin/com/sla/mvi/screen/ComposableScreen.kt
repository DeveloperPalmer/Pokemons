package com.sla.mvi.screen

import androidx.compose.runtime.Composable
import com.sla.mvi.BaseViewModel

data class ComposableScreen(
  val viewModel: BaseViewModel<*, *, *, *>,
  val content: @Composable (viewModel: BaseViewModel<*, *, *, *>) -> Unit,
)
