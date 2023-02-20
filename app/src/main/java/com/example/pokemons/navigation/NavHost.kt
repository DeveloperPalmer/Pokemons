package com.example.pokemons.navigation

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.feature.pokemons.routing.pokemonsGraph
import com.example.feature.pokemons.routing.pokemonsMainRoute
import com.example.pokemons.R

@Composable
fun PokemonsNavHost(
  navController: NavHostController,
  modifier: Modifier = Modifier,
) {
  NavHost(
    modifier = modifier,
    navController = navController,
    startDestination = pokemonsGraph
  ) {
    pokemonsGraph()
  }
}

enum class RootDestination(
  val id: String,
  @DrawableRes
  val iconRes: Int,
  val title: String,
) {
  Pokemons(
    id = pokemonsMainRoute,
    iconRes = R.drawable.ic_heart_broken_24,
    title = "Pokemons"
  ),
}