package com.pokemons.feature.pokemons.ui.di

import com.pokemons.feature.pokemons.domain.di.PokemonsScope
import com.pokemons.feature.pokemons.ui.navigation.PokemonsRoutes
import com.pokemons.feature.pokemons.ui.navigation.RouteKey
import com.pokemons.feature.pokemons.ui.screen.create.CreateAccountScreen
import com.pokemons.feature.pokemons.ui.screen.create.CreateAccountViewModel
import com.pokemons.feature.pokemons.ui.screen.login.LoginScreen
import com.pokemons.feature.pokemons.ui.screen.login.LoginViewModel
import com.pokemons.feature.pokemons.ui.screen.pokemons.PokemonsScreen
import com.pokemons.feature.pokemons.ui.screen.pokemons.PokemonsViewModel
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
  @RouteKey(PokemonsRoutes.Login)
  fun providesLoginScreen(model: LoginViewModel): ComposableScreen {
    return ComposableScreen(
      viewModel = model,
      content = { LoginScreen(model = model) }
    )
  }

  @Provides
  @IntoMap
  @RouteKey(PokemonsRoutes.CreateAccount)
  fun providesCreateAccountScreen(model: CreateAccountViewModel): ComposableScreen {
    return ComposableScreen(
      viewModel = model,
      content = { CreateAccountScreen(model = model) }
    )
  }

  @Provides
  @IntoMap
  @RouteKey(PokemonsRoutes.Pokemons)
  fun providesPokemonsScreen(model: PokemonsViewModel): ComposableScreen {
    return ComposableScreen(
      viewModel = model,
      content = { PokemonsScreen(model = model) }
    )
  }
}
