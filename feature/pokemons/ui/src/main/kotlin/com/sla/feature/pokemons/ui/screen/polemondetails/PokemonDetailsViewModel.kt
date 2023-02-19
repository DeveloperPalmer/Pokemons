package com.sla.feature.pokemons.ui.screen.polemondetails

import com.sla.feature.pokemons.domain.PokemonsModel
import com.sla.feature.pokemons.ui.navigation.FlowEvent
import com.sla.mvi.BaseViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.combine
import ru.dimsuz.unicorn.coroutines.MachineDsl
import javax.inject.Inject

class PokemonDetailsViewModel @Inject constructor(
  private val pokemonsModel: PokemonsModel,
  private val flowEvents: MutableSharedFlow<FlowEvent>
) : BaseViewModel<ViewState, ViewIntents, Unit, Unit>() {

  override fun MachineDsl<ViewState, Unit>.buildMachine() {
    initial = ViewState() to null

    onEach(intent(ViewIntents::navigateBack)) {
      action { _, _, _ ->
        pokemonsModel.clearActivePokemon()
        flowEvents.tryEmit(FlowEvent.PokemonDetailsDismissed)
      }
    }

    onEach(combine(pokemonsModel.activePokemon, pokemonsModel.pokemonSpecies, ::Pair)) {
      transitionTo { state, (pokemon, species) ->
        state.copy(pokemon = pokemon, species = species)
      }
    }

    onEach(intent(ViewIntents::switchFavorites)) {
      action { state, _, _ ->
        if (state.pokemon != null) {
          pokemonsModel.switchFavorites(state.pokemon.name)
        }
      }
    }
  }
}
