package com.example.pokemons.di

import android.content.Context
import com.pokemons.feature.core.domain.di.ApplicationContext
import com.pokemons.feature.core.domain.di.SingleIn
import com.squareup.anvil.annotations.ContributesTo
import com.squareup.anvil.annotations.MergeComponent
import dagger.BindsInstance
import dagger.Component
import dagger.Module

interface AppScope

@SingleIn(AppScope::class)
@MergeComponent(AppScope::class)
interface AppComponent {
  @Component.Builder
  interface Builder {
    @BindsInstance
    fun applicationContext(@ApplicationContext context: Context): Builder
    fun build(): AppComponent
  }
}