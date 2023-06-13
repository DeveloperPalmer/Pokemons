package com.pokemons.feature.pokemons.ui.navigation

sealed class FlowEvent {
  object CreateAccountRequested: FlowEvent()
}