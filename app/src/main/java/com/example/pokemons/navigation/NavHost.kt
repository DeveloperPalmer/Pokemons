package com.example.pokemons.navigation

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.feature.pokemons.routing.pokemonsGraph
import com.example.feature.pokemons.routing.pokemonsMainRoute
import com.example.pokemons.R

@Composable
fun PokemonsNavHost(
  navController: NavHostController
) {
  NavHost(
    navController =navController ,
    startDestination = pokemonsMainRoute
  ) {
    pokemonsGraph()
  }
}

enum class RootDestination(
  val id: String,
  @DrawableRes
  val iconRes: Int,
  val title: String,
){
  Pokemons(
    id = "root_pokemons",
    iconRes = R.drawable.ic_heart_broken_24,
    title = "Pokemons"
  )
}