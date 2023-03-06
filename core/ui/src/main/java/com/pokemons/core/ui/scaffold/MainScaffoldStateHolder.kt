package com.pokemons.core.ui.scaffold

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

interface MainScaffoldStateProvider {
  fun readMainScaffoldState(): MainScaffoldState
  val stateChanges: Flow<MainScaffoldState>
  fun accept(state: MainScaffoldState)
}

class MainScaffoldStateHolder : MainScaffoldStateProvider {
  private val _state = MutableStateFlow(rootState)

  override fun readMainScaffoldState(): MainScaffoldState = _state.value
  override val stateChanges: Flow<MainScaffoldState> = _state

  override fun accept(state: MainScaffoldState) = _state.update { state }
}
