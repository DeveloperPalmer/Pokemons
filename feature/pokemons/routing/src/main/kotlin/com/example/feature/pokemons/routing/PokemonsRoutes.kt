package com.example.feature.pokemons.routing

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation

const val pokemonsMainRoute = "pokemons_main_route"
const val pokemonsGraph = "pokemons_graph"

fun NavGraphBuilder.pokemonsGraph() {
  navigation(
    route = "pokemonsGraph",
    startDestination = pokemonsMainRoute
  ) {
    composable(route = pokemonsMainRoute) {}
  }
}