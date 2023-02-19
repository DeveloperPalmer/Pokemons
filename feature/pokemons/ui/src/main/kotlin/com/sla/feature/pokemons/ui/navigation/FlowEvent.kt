package com.sla.feature.pokemons.ui.navigation

sealed class FlowEvent {
  object ProfileRequested: FlowEvent()
  data class ProfileDismissed(val isLogout: Boolean): FlowEvent()

  object PokemonsRequested: FlowEvent()
  object PokemonsDismissed: FlowEvent()
  data class PokemonDetailsRequested(val name: String): FlowEvent()
  object PokemonDetailsDismissed: FlowEvent()
  object LoginRequested: FlowEvent()
}
