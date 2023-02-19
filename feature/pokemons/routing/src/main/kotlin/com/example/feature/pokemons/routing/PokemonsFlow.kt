package com.example.feature.pokemons.routing

import com.example.feature.pokemons.routing.di.PokemonsFlowComponent
import com.sla.core.routing.BaseFlowCoordinator1
import com.sla.core.routing.di.DestinationsIn
import com.sla.core.ui.scaffold.MainScaffoldController
import com.sla.core.ui.scaffold.popMainTo
import com.sla.core.ui.scaffold.pushMain
import com.sla.feature.core.domain.di.SingleIn
import com.sla.feature.login.routing.LoginFlow
import com.sla.feature.pokemons.domain.PokemonsModel
import com.sla.feature.pokemons.domain.di.PokemonsScope
import com.sla.feature.pokemons.ui.navigation.FlowEvent
import com.sla.feature.pokemons.ui.navigation.PokemonsRoutes
import com.sla.feature.profile.routing.ProfileFlow
import com.sla.mvi.screen.Destination
import com.sla.navigation.ScreenRegistry
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject

object PokemonsFlow {

  enum class Result {
    Dismissed
  }

  object Params

  @SingleIn(PokemonsScope::class)
  class Coordinator @Inject constructor(
    @DestinationsIn(PokemonsScope::class)
    destinations: Set<@JvmSuppressWildcards Destination>,
    flowEvents: MutableSharedFlow<FlowEvent>,
    screenRegistry: ScreenRegistry,
    private val pokemonsModel: PokemonsModel,
    private val controller: MainScaffoldController,
    private val component: PokemonsFlowComponent,
  ) : BaseFlowCoordinator1<FlowEvent, Params, Result>(
    controller, destinations, flowEvents, screenRegistry
  ) {

    init {
      pokemonsModel.start(coroutineScope)
      pokemonsModel.requestPokemons.start(1)
    }

    override fun onFlowStart(params: Params) {
      controller.pushMain(route = PokemonsRoutes.Pokemons)
    }

    override fun handleEvent(event: FlowEvent) {
      when (event) {
        is FlowEvent.PokemonsRequested -> {
          controller.pushMain(route = PokemonsRoutes.Pokemons)
        }
        is FlowEvent.PokemonsDismissed -> {
          finish(Result.Dismissed)
        }
        is FlowEvent.PokemonDetailsDismissed -> {
          controller.popMainTo(route = PokemonsRoutes.Pokemons)
        }
        is FlowEvent.PokemonDetailsRequested -> {
          pokemonsModel.requestPokemonAbilities.start(event.name)
          controller.pushMain(route = PokemonsRoutes.PokemonDetails)
        }
        is FlowEvent.ProfileDismissed -> {
          controller.popMainTo(route = PokemonsRoutes.Pokemons)
        }
        is FlowEvent.ProfileRequested -> {
          component.profileFlowComponent().coordinator().start(
            params = ProfileFlow.Params,
            onFlowFinish = {},
            onError = { ProfileFlow.Result.Dismissed }
          )
        }
        is FlowEvent.LoginRequested -> {
          component.loginFlowComponent().coordinator().start(
            params = LoginFlow.Params,
            onFlowFinish = {},
            onError = { LoginFlow.Result.Dismissed }
          )
        }
      }
    }
  }
}
