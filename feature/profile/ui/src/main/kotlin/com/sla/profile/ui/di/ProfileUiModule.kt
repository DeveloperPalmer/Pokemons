package com.sla.profile.ui.di

import com.sla.feature.profile.domain.di.ProfileScope
import com.sla.mvi.screen.ComposableScreen
import com.sla.profile.ui.navigation.ProfileRoutes
import com.sla.profile.ui.navigation.RouteKey
import com.sla.profile.ui.screen.edit.ProfileEditScreen
import com.sla.profile.ui.screen.edit.ProfileEditViewModel
import com.sla.profile.ui.screen.profile.ProfileScreen
import com.sla.profile.ui.screen.profile.ProfileViewModel
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
@ContributesTo(ProfileScope::class)
object ProfileUiModule {

  @Provides
  @IntoMap
  @RouteKey(ProfileRoutes.Profile)
  fun providesProfileScreen(model: ProfileViewModel): ComposableScreen {
    return ComposableScreen(
      viewModel = model,
      content = { ProfileScreen(model = model) }
    )
  }

  @Provides
  @IntoMap
  @RouteKey(ProfileRoutes.ProfileEdit)
  fun providesProfileEditScreen(model: ProfileEditViewModel): ComposableScreen {
    return ComposableScreen(
      viewModel = model,
      content = { ProfileEditScreen(model = model) }
    )
  }
}
