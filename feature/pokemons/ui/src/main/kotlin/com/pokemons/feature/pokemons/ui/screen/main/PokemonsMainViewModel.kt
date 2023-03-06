package com.pokemons.feature.pokemons.ui.screen.main

import com.pokemons.feature.pokemons.domain.PokemonsModel
import com.pokemons.feature.pokemons.ui.navigation.FlowEvent
import com.pokemons.mvi.BaseViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import ru.dimsuz.unicorn.coroutines.MachineDsl
import javax.inject.Inject


class PokemonsMainViewModel @Inject constructor(
  private val pokemonsModel: PokemonsModel,
  private val flowEvents: MutableSharedFlow<FlowEvent>
): BaseViewModel<ViewState, ViewIntents, Unit, Unit>() {

  override fun MachineDsl<ViewState, Unit>.buildMachine() {
    onEach(intent(ViewIntents::navigateBack)) {
      action { _, _, _ ->  }
    }
  }
}