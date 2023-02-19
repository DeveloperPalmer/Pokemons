package com.sla.feature.pokemons.ui.di

import com.sla.feature.pokemons.domain.di.PokemonsScope
import com.sla.feature.pokemons.ui.navigation.PokemonsRoutes
import com.sla.feature.pokemons.ui.navigation.RouteKey
import com.sla.feature.pokemons.ui.screen.pokemons.PokemonsScreen
import com.sla.feature.pokemons.ui.screen.pokemons.PokemonsViewModel
import com.sla.feature.pokemons.ui.screen.polemondetails.PokemonDetailsScreen
import com.sla.feature.pokemons.ui.screen.polemondetails.PokemonDetailsViewModel
import com.sla.mvi.screen.ComposableScreen
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
@ContributesTo(PokemonsScope::class)
object PokemonsUiModule {

  @Provides
  @IntoMap
  @RouteKey(PokemonsRoutes.Pokemons)
  fun providesPokemonsScreen(model: PokemonsViewModel): ComposableScreen {
    return ComposableScreen(
      viewModel = model,
      content = { PokemonsScreen(model = model) }
    )
  }

  @Provides
  @IntoMap
  @RouteKey(PokemonsRoutes.PokemonDetails)
  fun providesPokemonDetailsScreen(model: PokemonDetailsViewModel): ComposableScreen {
    return ComposableScreen(
      viewModel = model,
      content = { PokemonDetailsScreen(model = model) }
    )
  }
}
