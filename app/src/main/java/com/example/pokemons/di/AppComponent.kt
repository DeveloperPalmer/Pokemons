package com.example.pokemons.di

import android.content.Context
import com.pokemons.feature.core.domain.di.ApplicationContext
import com.pokemons.feature.core.domain.di.SingleIn
import com.sla.feature.app.AppScope
import com.squareup.anvil.annotations.MergeComponent
import dagger.BindsInstance
import dagger.Component
import io.ktor.client.HttpClient

@SingleIn(AppScope::class)
@MergeComponent(AppScope::class)
interface AppComponent {
  @Component.Builder
  interface Builder {
    @BindsInstance
    fun applicationContext(@ApplicationContext context: Context): Builder
    @BindsInstance
    fun httpClient(httpClient: HttpClient): Builder
    fun build(): AppComponent
  }
}
interface AppComponentHolder {
  val appComponent: AppComponent
}
