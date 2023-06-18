package com.pokemons.feature.pokemons.ui.screen.pokemons

import com.github.michaelbull.result.fold
import com.pokemons.feature.pokemons.domain.PokemonsModel
import com.pokemons.feature.pokemons.ui.navigation.FlowEvent
import com.pokemons.mvi.BaseViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import ru.dimsuz.unicorn.coroutines.MachineDsl
import javax.inject.Inject

class PokemonsViewModel @Inject constructor(
  private val model: PokemonsModel,
  private val flowEvents: MutableSharedFlow<FlowEvent>
) : BaseViewModel<ViewState, ViewIntents, Unit, Unit>() {

  override fun MachineDsl<ViewState, Unit>.buildMachine() {
    initial = ViewState() to { model.pokemons.start(1) }

    onEach(intent(ViewIntents::navigateBack)) {
      action { _, _, _ ->
        model.cancel()
        flowEvents.tryEmit(FlowEvent.PokemonsDismissed)
      }
    }

    onEach(model.pokemons.jobFlow.results()) {
      transitionTo { state, results ->
        results.fold(
          success = { pokemons -> state.copy(pokemons = pokemons) },
          failure = { state }
        )
      }
    }
  }
}
