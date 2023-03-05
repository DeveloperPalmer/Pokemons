package com.pokemons.navigation

import com.pokemons.mvi.screen.ComposableScreen
import com.pokemons.mvi.screen.Destination
import com.pokemons.mvi.screen.Route

interface ScreenRegistry {
  fun createScreen(route: Route)
  fun destroyScreens(predicate: (Route) -> Boolean)

  fun register(destinations: Collection<Destination>)
  fun unregister(destinations: Collection<Destination>)

  fun screen(route: Route): ComposableScreen?
}
