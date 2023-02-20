package com.example.feature.pokemons.routing

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation

fun NavGraphBuilder.pokemonsGraph() {
  navigation(
    startDestination = pokemonsMainRoute,
    route = pokemonsGraph
  )  {
    composable(route = pokemonsMainRoute) {
      Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
      ) {
        Text(text = "Pokemons")
      }
    }
    composable(route = pokemonsSearchRoute) {
      Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
      ) {
        Text(text = "Pokemons Search")
      }
    }
  }
}

const val pokemonsGraph = "pokemons_graph"
const val pokemonsMainRoute = "pokemons_main_route"
const val pokemonsSearchRoute = "pokemons_search_route"
