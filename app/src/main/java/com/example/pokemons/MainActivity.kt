package com.example.pokemons

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.pokemons.navigation.PokemonsNavHost
import com.example.pokemons.ui.theme.PokemonsTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      PokemonsTheme {
        Scaffold { padding ->
          Box(
            modifier = Modifier
              .fillMaxSize()
              .padding(padding)
          ) {
            PokemonsNavHost(
              navController = rememberNavController()
            )
          }
        }
      }
    }
  }
}
