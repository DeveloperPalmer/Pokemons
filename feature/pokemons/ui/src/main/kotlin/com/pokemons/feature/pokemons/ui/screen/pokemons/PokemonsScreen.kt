package com.pokemons.feature.pokemons.ui.screen.pokemons

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.pokemons.core.ui.screen.MviScreen
import com.pokemons.core.ui.screen.OnBackPressedHandler
import com.pokemons.feature.pokemons.domain.entity.Pokemon
import com.pokemons.feature.pokemons.ui.screen.login.PasswordSecurity
import com.pokemons.mvi.BaseViewIntents
import com.pokemons.mvi.rememberViewIntents

@Composable
fun PokemonsScreen(model: PokemonsViewModel) {
  MviScreen(
    viewModel = model,
    intents = rememberViewIntents(),
  ) { state, intent ->
    OnBackPressedHandler(onBack = intent.navigateBack)
    Box(
      modifier = Modifier.fillMaxSize(),
      contentAlignment = Alignment.Center
    ) {
      if (state.pokemons == null) {
        CircularProgressIndicator()
      } else {
        LazyVerticalGrid(
          columns = GridCells.Fixed(2)
        ) {
          items(state.pokemons) {
            Text(text = it.name)
          }
        }
      }
    }
  }
}

class ViewIntents : BaseViewIntents() {
  val navigateBack = intent("navigateBack")
}

@Immutable
data class ViewState(
  val pokemons: List<Pokemon>? = null,
)