package com.sla.profile.ui.navigation

import com.sla.mvi.screen.Route
import dagger.MapKey

enum class ProfileRoutes(override val path: String) : Route {
  Profile("pokemons/profile"),
  ProfileEdit("pokemons/profile-edit"),
}

@MapKey
annotation class RouteKey(val value: ProfileRoutes)
