package com.pokemons.feature.pokemons.ui.navigation

import com.pokemons.mvi.screen.Route
import dagger.MapKey

enum class PokemonsRoutes(override val path: String) : Route {
  Login("pokemons/login"),
  CreateAccount("pokemons/create-account"),
}

@MapKey
annotation class RouteKey(val value: PokemonsRoutes)
