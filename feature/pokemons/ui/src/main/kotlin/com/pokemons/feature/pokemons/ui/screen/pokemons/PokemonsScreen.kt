package com.pokemons.feature.pokemons.ui.screen.pokemons

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.pokemons.core.ui.screen.MviScreen
import com.pokemons.core.ui.screen.OnBackPressedHandler
import com.pokemons.core.ui.theme.AppTheme
import com.pokemons.core.uikit.surface
import com.pokemons.feature.pokemons.domain.entity.Pokemon
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
          columns = GridCells.Fixed(2),
          contentPadding = PaddingValues(16.dp),
          verticalArrangement = Arrangement.spacedBy(16.dp),
          horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          items(state.pokemons) {
            PokemonItem(
              modifier = Modifier,
              pokemon = it,
              onClick = {}
            )
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

@Composable
private fun PokemonItem(
  pokemon: Pokemon,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val shape = RoundedCornerShape(12.dp)
  Box(
    modifier = modifier
      .border(
        width = 1.dp,
        color = AppTheme.colors.greyLight,
        shape = shape
      )
      .surface(
        elevation = 1.dp,
        backgroundColor = AppTheme.colors.white,
        shape = shape,
        onClick = onClick
      )
      .padding(16.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      AsyncImage(
        modifier = Modifier.size(124.dp),
        model = ImageRequest.Builder(LocalContext.current)
          .data(pokemon.avatar)
          .decoderFactory(SvgDecoder.Factory())
          .build(),
        contentDescription = null
      )
      Text(
        text = pokemon.name,
        color = AppTheme.colors.textPrimary,
        style = AppTheme.typography.body
      )
    }
  }
}