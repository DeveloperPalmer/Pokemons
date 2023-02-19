package com.sla.feature.profile.routing.di

import com.sla.core.routing.di.DestinationsIn
import com.sla.core.routing.di.mapToDestinations
import com.sla.feature.core.domain.di.SingleIn
import com.sla.feature.profile.domain.di.ProfileScope
import com.sla.feature.profile.routing.ProfileFlow
import com.sla.mvi.screen.ComposableScreen
import com.sla.mvi.screen.Destination
import com.sla.profile.ui.navigation.FlowEvent
import com.sla.profile.ui.navigation.ProfileRoutes
import com.squareup.anvil.annotations.ContributesTo
import com.squareup.anvil.annotations.MergeSubcomponent
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Provider

@SingleIn(ProfileScope::class)
@MergeSubcomponent(ProfileScope::class)
interface ProfileFlowComponent {
  fun coordinator(): ProfileFlow.Coordinator
}

@Module
@ContributesTo(ProfileScope::class)
object ProfileModule {
  @Provides
  @DestinationsIn(ProfileScope::class)
  @SingleIn(ProfileScope::class)
  fun provideDestinations(
    screens: Map<ProfileRoutes, @JvmSuppressWildcards Provider<ComposableScreen>>,
  ): Set<Destination> {
    return screens.mapToDestinations()
  }

  @Provides
  @SingleIn(ProfileScope::class)
  fun providesFlowEvents(): MutableSharedFlow<FlowEvent> {
    return MutableSharedFlow(extraBufferCapacity = 3)
  }
}
