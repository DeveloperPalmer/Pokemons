package com.sla.feature.pokemons.ui.screen.polemondetails

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.sla.core.ui.screen.MviScreen
import com.sla.core.ui.screen.OnBackPressedHandler
import com.sla.core.ui.theme.AppTheme
import com.sla.feature.pokemons.domain.entity.Pokemon
import com.sla.feature.pokemons.domain.entity.PokemonSpecies
import com.sla.mvi.BaseViewIntents
import com.sla.mvi.rememberViewIntents

@Composable
fun PokemonDetailsScreen(model: PokemonDetailsViewModel) {
  MviScreen(
    viewModel = model,
    intents = rememberViewIntents(),
  ) { state, intent ->
    OnBackPressedHandler(onBack = intent.navigateBack)
    if (state.pokemon == null || state.species == null) {
      Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
      ) {
        CircularProgressIndicator()
      }
    } else {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .statusBarsPadding()
          .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
//        TopAppBar(
//          modifier = Modifier
//            .heightIn(52.dp)
//            .padding(horizontal = 4.dp),
//          title = null,
//          onProfileClick = intent.navigateBack,
//          trailingContent = {
//            FavoritesButton(
//              isActive = state.pokemon.isFavorites,
//              onClick = intent.switchFavorites
//            )
//          },
//          onSignIn = {}
//        )
        PokemonCard(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          avatarUrl = state.pokemon.avatar,
          name = state.pokemon.name
        )
        Text(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          text = state.species.effects,
          color = AppTheme.colors.textPrimary,
          style = AppTheme.typography.body2,
          textAlign = TextAlign.Center
        )
      }
    }
  }
}

class ViewIntents : BaseViewIntents() {
  val navigateBack = intent(name = "navigateBack")
  val switchFavorites = intent("changeFavorites")
}

@Immutable
data class ViewState(
  val pokemon: Pokemon? = null,
  val species: PokemonSpecies? = null,
)

@Composable
private fun PokemonCard(
  avatarUrl: String,
  name: String,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier
      .border(1.dp, AppTheme.colors.greyLight, RoundedCornerShape(16.dp))
      .padding(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    AsyncImage(
      modifier = Modifier.size(124.dp),
      model = ImageRequest.Builder(LocalContext.current)
        .data(avatarUrl)
        .decoderFactory(SvgDecoder.Factory())
        .build(),
      contentDescription = null
    )
    Text(
      text = name,
      color = AppTheme.colors.textPrimary,
      style = AppTheme.typography.body
    )
  }
}

