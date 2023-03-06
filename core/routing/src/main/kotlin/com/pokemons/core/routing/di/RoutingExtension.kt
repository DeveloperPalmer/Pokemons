package com.pokemons.core.routing.di

import com.pokemons.mvi.screen.ComposableScreen
import com.pokemons.mvi.screen.Destination
import com.pokemons.mvi.screen.Route
import javax.inject.Provider

fun Map<out Route, Provider<ComposableScreen>>.mapToDestinations(): Set<Destination> {
  return entries.mapTo(mutableSetOf()) { (route, screen) -> Destination(route, screen) }
}
