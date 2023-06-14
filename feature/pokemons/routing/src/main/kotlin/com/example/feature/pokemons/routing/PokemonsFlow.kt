package com.example.feature.pokemons.routing

import com.pokemons.core.routing.BaseFlowCoordinator
import com.pokemons.core.routing.di.DestinationsIn
import com.pokemons.core.ui.scaffold.MainScaffoldController
import com.pokemons.core.ui.scaffold.popMainTo
import com.pokemons.core.ui.scaffold.pushMain
import com.pokemons.feature.core.domain.di.SingleIn
import com.pokemons.feature.pokemons.domain.PokemonsModel
import com.pokemons.feature.pokemons.domain.di.PokemonsScope
import com.pokemons.feature.pokemons.ui.navigation.FlowEvent
import com.pokemons.feature.pokemons.ui.navigation.PokemonsRoutes
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
    private val model: PokemonsModel,
  ) : BaseFlowCoordinator<FlowEvent, Result>(
    controller, destinations, flowEvents, screenRegistry
  ) {

    init {
      model.start(coroutineScope)
    }

    override fun onFlowStart() {
      controller.pushMain(route = PokemonsRoutes.Login)
    }

    override fun handleEvent(event: FlowEvent) {
      when(event) {
        is FlowEvent.CreateAccountRequested -> {
          controller.pushMain(route = PokemonsRoutes.CreateAccount)
        }
        is FlowEvent.CreateAccountDismissed -> {
          controller.popMainTo(route = PokemonsRoutes.Login)
        }
        is FlowEvent.PokemonsRequested -> {
          controller.pushMain(route = PokemonsRoutes.Pokemons)
        }
        is FlowEvent.PokemonsDismissed -> {
          controller.popMainTo(route = PokemonsRoutes.CreateAccount)
        }
      }
    }
  }
}
