package com.sla.feature.pokemons.ui.screen.pokemons

import com.sla.feature.pokemons.domain.PokemonsModel
import com.sla.feature.pokemons.ui.navigation.FlowEvent
import com.sla.mvi.BaseViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import ru.dimsuz.unicorn.coroutines.MachineDsl
import ru.kode.remo.JobState
import javax.inject.Inject

class PokemonsViewModel @Inject constructor(
  private val pokemonsModel: PokemonsModel,
  private val flowEvents: MutableSharedFlow<FlowEvent>
) : BaseViewModel<ViewState, ViewIntents, Unit, Unit>() {

  override fun MachineDsl<ViewState, Unit>.buildMachine() {
    initial = ViewState() to null

    onEach(pokemonsModel.pokemons) {
      transitionTo { state, pokemons ->
        state.copy(
          defaultPokemons = pokemons,
          favoritesPokemons = pokemons.filter { it.isFavorites }
        )
      }
    }

    onEach(pokemonsModel.isAuthorized) {
      transitionTo { state, isAuthorized ->
        state.copy(isAuthorized = isAuthorized)
      }
    }

    onEach(typedIntent(ViewIntents::switchFavorites)) {
      action { _, _, name ->
        pokemonsModel.switchFavorites(name)
      }
    }

    onEach(pokemonsModel.requestPokemons.jobFlow.state) {
      transitionTo { state, jobState ->
        state.copy(isLoading = jobState == JobState.Running)
      }
    }

    onEach(typedIntent(ViewIntents::openPokemon)) {
      action { _, _, name ->
        flowEvents.tryEmit(FlowEvent.PokemonDetailsRequested(name))
      }
    }

    configureSignInTransitions()
    configureProfileTransitions()
  }

  private fun MachineDsl<ViewState, Unit>.configureProfileTransitions() {
    onEach(intent(ViewIntents::openProfile)) {
      action { _, _, _ ->
        flowEvents.tryEmit(FlowEvent.ProfileRequested)
      }
    }
  }

  private fun MachineDsl<ViewState, Unit>.configureSignInTransitions() {
    onEach(intent(ViewIntents::signIn)) {
      action { _, _, _ ->
        flowEvents.tryEmit(FlowEvent.LoginRequested)
      }
    }
  }
}
