package com.sla.feature.pokemons.ui.navigation

import com.sla.mvi.screen.Route
import dagger.MapKey

enum class PokemonsRoutes(override val path: String) : Route {
  Pokemons("pokemons/pokemons"),
  PokemonDetails("pokemons/pokemon-details"),
}

@MapKey
annotation class RouteKey(val value: PokemonsRoutes)
