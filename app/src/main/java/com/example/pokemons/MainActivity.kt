package com.example.pokemons

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.example.pokemons.navigation.PokemonsBottomBar
import com.example.pokemons.navigation.PokemonsNavHost
import com.example.pokemons.navigation.RootDestination
import com.example.pokemons.ui.theme.PokemonsTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      PokemonsTheme {
        val navController = rememberNavController()
        Scaffold(
          backgroundColor = Color.Transparent,
          content = { padding ->
            PokemonsNavHost(
              modifier = Modifier
                .fillMaxSize()
                .padding(padding),
              navController = navController
            )
          },
          bottomBar = {
            PokemonsBottomBar(
              destinations = RootDestination.values().asList(),
              onNavigateToDestination = { destination ->
                navController.navigate(
                  route = destination.id,
                  navOptions = navOptions { launchSingleTop = true; restoreState = true }
                )
              }
            )
          },
        )
      }
    }
  }
}
