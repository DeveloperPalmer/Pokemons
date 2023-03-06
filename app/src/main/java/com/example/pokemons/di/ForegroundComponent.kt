package com.example.pokemons.di

import android.app.Activity
import com.example.feature.pokemons.routing.di.PokemonsFlowComponent
import com.pokemons.core.ui.scaffold.MainScaffoldController
import com.pokemons.feature.bottombar.ui.BottomBarController
import com.pokemons.feature.core.domain.di.ActivityContext
import com.pokemons.feature.core.domain.di.SingleIn
import com.pokemons.navigation.ScreenRegistry
import com.squareup.anvil.annotations.ContributesSubcomponent
import com.squareup.anvil.annotations.ContributesTo
import dagger.BindsInstance
import dagger.Module
import dagger.Provides
import javax.inject.Qualifier

interface ForegroundScope

@SingleIn(ForegroundScope::class)
@ContributesSubcomponent(ForegroundScope::class, parentScope = AppScope::class)
interface ForegroundComponent {

  @ContributesTo(AppScope::class)
  interface ParentComponent {
    fun foregroundComponentFactory(): Factory
  }

  fun pokemonsFlowComponent(): PokemonsFlowComponent

  fun bottomBarController(): BottomBarController
  fun mainScaffoldController(): MainScaffoldController
  fun screenRegistry(): ScreenRegistry

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

@Module
@ContributesTo(ForegroundScope::class)
object ForegroundModule {

  @Provides
  @SingleIn(ForegroundScope::class)
  fun provideBottomBarController(): BottomBarController = BottomBarController()
}

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class MainNavigation

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class BottomSheetNavigation

