package com.sla.feature.login.routing.di

import com.sla.core.routing.di.DestinationsIn
import com.sla.core.routing.di.mapToDestinations
import com.sla.feature.core.domain.di.SingleIn
import com.sla.mvi.screen.ComposableScreen
import com.sla.mvi.screen.Destination
import com.sla.feature.login.domain.di.LoginScope
import com.sla.feature.login.routing.LoginFlow
import com.sla.feature.login.ui.navigation.FlowEvent
import com.sla.feature.login.ui.navigation.LoginRoute
import com.squareup.anvil.annotations.ContributesTo
import com.squareup.anvil.annotations.MergeSubcomponent
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Provider

@SingleIn(LoginScope::class)
@MergeSubcomponent(LoginScope::class)
interface LoginFlowComponent {
  fun coordinator(): LoginFlow.Coordinator
}


@Module
@ContributesTo(LoginScope::class)
object LoginFlowModule {

  @Provides
  @DestinationsIn(LoginScope::class)
  @SingleIn(LoginScope::class)
  fun provideDestinations(
    screens: Map<LoginRoute, @JvmSuppressWildcards Provider<ComposableScreen>>,
  ): Set<Destination> {
    return screens.mapToDestinations()
  }

  @Provides
  @SingleIn(LoginScope::class)
  fun providesFlowEvents(): MutableSharedFlow<FlowEvent> {
    return MutableSharedFlow(extraBufferCapacity = 3)
  }
}
