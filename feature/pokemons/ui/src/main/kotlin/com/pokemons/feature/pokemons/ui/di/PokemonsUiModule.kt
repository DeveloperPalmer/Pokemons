package com.pokemons.feature.pokemons.ui.di

import com.pokemons.feature.pokemons.domain.di.PokemonsScope
import com.pokemons.feature.pokemons.ui.navigation.PokemonsRoutes
import com.pokemons.feature.pokemons.ui.navigation.RouteKey
import com.pokemons.feature.pokemons.ui.screen.main.PokemonsMainScreen
import com.pokemons.feature.pokemons.ui.screen.main.PokemonsMainViewModel
import com.pokemons.mvi.screen.ComposableScreen
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
@ContributesTo(PokemonsScope::class)
object PokemonsUiModule {

  @Provides
  @IntoMap
  @RouteKey(PokemonsRoutes.PokemonsMain)
  fun providesPokemonsMain(model: PokemonsMainViewModel): ComposableScreen {
    return ComposableScreen(
      viewModel = model,
      content = { PokemonsMainScreen(model = model) }
    )
  }
}
