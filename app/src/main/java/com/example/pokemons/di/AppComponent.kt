package com.example.pokemons.di

import android.content.Context
import com.squareup.anvil.annotations.ContributesTo
import com.squareup.anvil.annotations.MergeComponent
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import javax.inject.Qualifier
import javax.inject.Scope
import javax.inject.Singleton
import kotlin.reflect.KClass


interface AppScope

@Singleton
@MergeComponent(AppScope::class)
interface AppComponent {
  @Component.Builder
  interface Builder {
    @BindsInstance
    fun applicationContext(@ApplicationContext context: Context): Builder
    fun build(): AppComponent
  }
}

@Module
@ContributesTo(AppScope::class)
object AppModule

/**
 * Indicates that this provided type (via [Provides], [Binds], [Inject], etc)
 * will only have a single instances within the target [value] scope.
 *
 * Note that the [value] does not actually need to be a [Scope]-annotated
 * annotation class. It is _solely_ a key.
 */
@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class SingleIn(val value: KClass<*>)

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ActivityContext

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ApplicationContext
