package com.pokemons.feature.pokemons.ui.navigation

sealed class FlowEvent {
  object CreateAccountRequested: FlowEvent()
  object CreateAccountDismissed: FlowEvent()
  object PokemonsRequested: FlowEvent()
  object PokemonsDismissed: FlowEvent()
}
