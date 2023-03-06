package com.pokemons.feature.pokemons.ui.screen.main

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import com.pokemons.core.ui.screen.MviScreen
import com.pokemons.mvi.BaseViewIntents
import com.pokemons.mvi.rememberViewIntents


@Composable
fun PokemonsMainScreen(model: PokemonsMainViewModel) {
  MviScreen(
    viewModel = model,
    intents = rememberViewIntents(),
  ) { _, _ ->
    Box(modifier = Modifier)
  }
}

class ViewIntents : BaseViewIntents() {
  val navigateBack = intent("navigateBack")
}

@Immutable
data class ViewState(
  val bookingUrlPath: String? = null,
)
