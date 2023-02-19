package com.sla.feature.pokemons.ui.screen.pokemons

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sla.core.ui.R
import com.sla.core.ui.screen.MviScreen
import com.sla.core.ui.theme.AppTheme
import com.sla.feature.pokemons.domain.entity.Pokemon
import com.sla.feature.pokemons.ui.component.PokemonItem
import com.sla.feature.pokemons.ui.component.PokemonTabs
import com.sla.feature.pokemons.ui.component.TopAppBar
import com.sla.mvi.BaseViewIntents
import com.sla.mvi.rememberViewIntents
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PokemonsScreen(model: PokemonsViewModel) {
  MviScreen(
    viewModel = model,
    intents = rememberViewIntents(),
  ) { state, intent ->
    Column(
      modifier = Modifier.fillMaxSize(),
    ) {
      if (state.isLoading) {
        Box(
          modifier = Modifier.fillMaxSize(),
          contentAlignment = Alignment.Center
        ) {
          CircularProgressIndicator(
            color = AppTheme.colors.primary
          )
        }
      } else {
        TopAppBar(
          modifier = Modifier.padding(8.dp),
          title = stringResource(id = R.string.my_collection),
          isAuthorized = state.isAuthorized,
          onProfileClick = intent.openProfile,
          onSignIn = intent.signIn
        )
        val scope = rememberCoroutineScope()
        val pagerState = rememberPagerState()
        PokemonTabs(
          currentPage = pagerState.currentPage,
          onPageChanges = { scope.launch { pagerState.scrollToPage(it) } }
        )
        HorizontalPager(
          pageCount = 2,
          state = pagerState,
          userScrollEnabled = false
        ) {
          LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
          ) {
            when (pagerState.currentPage) {
              0 -> items(state.defaultPokemons) { pokemon ->
                PokemonItem(
                  modifier = Modifier,
                  pokemon = pokemon,
                  onClick = { intent.openPokemon(pokemon.name) },
                  onFavoritesClick = { intent.switchFavorites(pokemon.name) },
                  isFavorites = pokemon.isFavorites
                )
              }
              else -> items(state.favoritesPokemons) { pokemon ->
                PokemonItem(
                  modifier = Modifier,
                  pokemon = pokemon,
                  onClick = { intent.openPokemon(pokemon.name) },
                  onFavoritesClick = { intent.switchFavorites(pokemon.name) },
                  isFavorites = pokemon.isFavorites
                )
              }
            }
          }
        }
      }
    }
  }
}

class ViewIntents : BaseViewIntents() {
  val openPokemon = intent<String>(name = "openPokemon")
  val switchFavorites = intent<String>("changeFavorites")
  val openProfile = intent(name = "openProfile")
  val signIn = intent(name = "signIn")
}

@Immutable
data class ViewState(
  val isAuthorized: Boolean = false,
  val isLoading: Boolean = false,
  val defaultPokemons: List<Pokemon> = emptyList(),
  val favoritesPokemons: List<Pokemon> = emptyList(),
)
