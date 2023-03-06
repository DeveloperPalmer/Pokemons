package com.example.feature.pokemons.routing.di

import com.example.feature.pokemons.routing.PokemonsFlow
import com.pokemons.core.routing.di.DestinationsIn
import com.pokemons.core.routing.di.mapToDestinations
import com.pokemons.feature.core.domain.di.SingleIn
import com.pokemons.feature.pokemons.domain.di.PokemonsScope
import com.pokemons.feature.pokemons.ui.navigation.FlowEvent
import com.pokemons.feature.pokemons.ui.navigation.PokemonsRoutes
import com.pokemons.mvi.screen.ComposableScreen
import com.pokemons.mvi.screen.Destination
import com.squareup.anvil.annotations.ContributesTo
import com.squareup.anvil.annotations.MergeSubcomponent
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Provider

@SingleIn(PokemonsScope::class)
@MergeSubcomponent(PokemonsScope::class)
interface PokemonsFlowComponent {
  fun coordinator(): PokemonsFlow.Coordinator
}

@Module
@ContributesTo(PokemonsScope::class)
object PokemonsModule {
  @Provides
  @DestinationsIn(PokemonsScope::class)
  @SingleIn(PokemonsScope::class)
  fun provideDestinations(
    screens: Map<PokemonsRoutes, @JvmSuppressWildcards Provider<ComposableScreen>>,
  ): Set<Destination> {
    return screens.mapToDestinations()
  }

  @Provides
  @SingleIn(PokemonsScope::class)
  fun providesFlowEvents(): MutableSharedFlow<FlowEvent> {
    return MutableSharedFlow(extraBufferCapacity = 3)
  }
}
