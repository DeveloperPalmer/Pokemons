package com.sla.core.routing.di

import com.sla.mvi.screen.ComposableScreen
import com.sla.mvi.screen.Destination
import com.sla.mvi.screen.Route
import javax.inject.Provider

fun Map<out Route, Provider<ComposableScreen>>.mapToDestinations(): Set<Destination> {
  return entries.mapTo(mutableSetOf()) { (route, screen) -> Destination(route, screen) }
}
