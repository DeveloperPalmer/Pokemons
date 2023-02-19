package com.example.pokemons.di

import android.content.Context
import androidx.room.Room
import com.example.pokemons.database.AppDatabase
import com.sla.feature.app.domain.AppScope
import com.sla.feature.auth.data.storage.AccountDao
import com.sla.feature.core.domain.di.ApplicationContext
import com.sla.feature.core.domain.di.SingleIn
import com.squareup.anvil.annotations.ContributesTo
import com.squareup.anvil.annotations.MergeComponent
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
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

@Module
@ContributesTo(AppScope::class)
object AppModule {

  @Provides
  fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
    return Room
      .databaseBuilder(context, AppDatabase::class.java, "database.db")
      .build()
  }

  @Provides
  fun provideAccountDao(database: AppDatabase): AccountDao {
    return database.accountDao()
  }
}

interface AppComponentHolder {
  val appComponent: AppComponent
}
