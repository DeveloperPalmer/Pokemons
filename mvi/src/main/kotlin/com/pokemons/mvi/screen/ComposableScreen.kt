package com.pokemons.mvi.screen

import androidx.compose.runtime.Composable
import com.pokemons.mvi.BaseViewModel

data class ComposableScreen(
  val viewModel: BaseViewModel<*, *, *, *>,
  val content: @Composable (viewModel: BaseViewModel<*, *, *, *>) -> Unit,
)
