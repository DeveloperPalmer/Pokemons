package com.example.pokemons.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.pokemons.R

@Composable
fun PokemonsBottomBar(
  destinations: List<RootDestination>,
  onNavigateToDestination: (RootDestination) -> Unit,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.SpaceEvenly,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    destinations.forEach { tab ->
      Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        IconButton(
          onClick = { onNavigateToDestination(tab) }
        ) {
          Icon(
            painter = painterResource(id = tab.iconRes),
            tint = MaterialTheme.colors.primary,
            contentDescription = null
          )
        }
        Text(
          text = tab.title
        )
      }
    }
  }
}