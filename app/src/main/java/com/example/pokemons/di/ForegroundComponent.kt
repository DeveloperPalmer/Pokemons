package com.example.pokemons.di

import android.app.Activity
import com.example.feature.pokemons.routing.di.PokemonsFlowComponent
import com.sla.core.ui.scaffold.MainScaffoldController
import com.sla.feature.app.domain.AppScope
import com.sla.feature.core.domain.di.ActivityContext
import com.sla.feature.core.domain.di.SingleIn
import com.sla.navigation.ScreenRegistry
import com.squareup.anvil.annotations.ContributesSubcomponent
import com.squareup.anvil.annotations.ContributesTo
import dagger.BindsInstance

interface ForegroundScope

@SingleIn(ForegroundScope::class)
@ContributesSubcomponent(ForegroundScope::class, parentScope = AppScope::class)
interface ForegroundComponent {

  @ContributesTo(AppScope::class)
  interface ParentComponent {
    fun foregroundComponentFactory(): Factory
  }

  fun pokemonsFlowComponent(): PokemonsFlowComponent

  @ContributesSubcomponent.Factory
  interface Factory {
    fun create(
      @BindsInstance
      @ActivityContext activity: Activity,
      @BindsInstance screenRegistry: ScreenRegistry,
      @BindsInstance mainScaffoldController: MainScaffoldController,
    ): ForegroundComponent
  }
}
