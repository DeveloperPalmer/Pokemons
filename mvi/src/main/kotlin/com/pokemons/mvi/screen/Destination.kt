package com.pokemons.mvi.screen

import ru.dimsuz.unicorn.coroutines.MachineDsl
import com.pokemons.mvi.BaseViewIntents
import com.pokemons.mvi.BaseViewModel
import javax.inject.Provider

data class Destination(
  val route: Route,
  val screen: Provider<ComposableScreen>,
) {
  companion object {
    val root = Destination(
      route = object : Route {
        override val path: String = "root"
      }
    ) {
      ComposableScreen(
        viewModel = object : BaseViewModel<Unit, BaseViewIntents, Unit, Unit>() {
          override fun MachineDsl<Unit, Unit>.buildMachine() = Unit
        },
        content = { }
      )
    }
  }
}
