package com.sla.navigation

import com.sla.mvi.screen.ComposableScreen
import com.sla.mvi.screen.Destination
import com.sla.mvi.screen.Route

interface ScreenRegistry {
  fun createScreen(route: Route)
  fun destroyScreens(predicate: (Route) -> Boolean)

  fun register(destinations: Collection<Destination>)
  fun unregister(destinations: Collection<Destination>)

  fun screen(route: Route): ComposableScreen?
}
