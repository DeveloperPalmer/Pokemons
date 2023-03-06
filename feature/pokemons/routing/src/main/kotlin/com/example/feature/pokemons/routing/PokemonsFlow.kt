package com.example.feature.pokemons.routing

import com.pokemons.core.routing.BaseFlowCoordinator
import com.pokemons.core.routing.di.DestinationsIn
import com.pokemons.core.ui.scaffold.MainScaffoldController
import com.pokemons.feature.core.domain.di.SingleIn
import com.pokemons.feature.pokemons.domain.di.PokemonsScope
import com.pokemons.feature.pokemons.ui.navigation.FlowEvent
import com.pokemons.mvi.screen.Destination
import com.pokemons.navigation.ScreenRegistry
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject

object PokemonsFlow {
  enum class Result {
    Dismissed
  }

  @SingleIn(PokemonsScope::class)
  class Coordinator @Inject constructor(
    @DestinationsIn(PokemonsScope::class)
    destinations: Set<@JvmSuppressWildcards Destination>,
    flowEvents: MutableSharedFlow<FlowEvent>,
    screenRegistry: ScreenRegistry,
    private val controller: MainScaffoldController,
  ) : BaseFlowCoordinator<FlowEvent, Result>(
    controller, destinations, flowEvents, screenRegistry
  ) {
    override fun handleEvent(event: FlowEvent) {
      when(event) {
        else -> Unit
      }
    }
  }
}
