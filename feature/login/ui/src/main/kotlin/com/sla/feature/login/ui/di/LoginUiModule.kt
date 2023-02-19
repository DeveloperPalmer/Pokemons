package com.sla.feature.login.ui.di

import com.sla.mvi.screen.ComposableScreen
import com.sla.feature.login.domain.di.LoginScope
import com.sla.feature.login.ui.navigation.LoginRoute
import com.sla.feature.login.ui.navigation.RouteKey
import com.sla.feature.login.ui.screen.create.CreateAccountScreen
import com.sla.feature.login.ui.screen.create.CreateAccountViewModel
import com.sla.feature.login.ui.screen.login.LoginScreen
import com.sla.feature.login.ui.screen.login.LoginViewModel
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
@ContributesTo(LoginScope::class)
object LoginUiModule {
  @Provides
  @IntoMap
  @RouteKey(LoginRoute.Login)
  fun providesLoginScreen(model: LoginViewModel): ComposableScreen {
    return ComposableScreen(
      viewModel = model,
      content = { LoginScreen(model = model) }
    )
  }

  @Provides
  @IntoMap
  @RouteKey(LoginRoute.CreateAccount)
  fun providesCreateAccountScreen(model: CreateAccountViewModel): ComposableScreen {
    return ComposableScreen(
      viewModel = model,
      content = { CreateAccountScreen(model = model) }
    )
  }
}
