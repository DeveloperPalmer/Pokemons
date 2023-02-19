package com.sla.feature.login.ui.navigation

import com.sla.mvi.screen.Route
import dagger.MapKey

enum class LoginRoute(override val path: String) : Route {
  Login("pokemons/login"),
  CreateAccount("pokemons/create-account"),
}

@MapKey
annotation class RouteKey(val value: LoginRoute)
