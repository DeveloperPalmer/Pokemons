package com.pokemons.feature.pokemons.data.di

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.pokemons.feature.core.domain.di.SingleIn
import com.pokemons.feature.pokemons.data.api.PokemonsApi
import com.pokemons.feature.pokemons.domain.di.PokemonsScope
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@ContributesTo(PokemonsScope::class)
object PokemonsDataModule {

  @Provides
  @SingleIn(PokemonsScope::class)
  fun providePokemonsApi(): PokemonsApi =
    Retrofit.Builder()
      .baseUrl("https://pokeapi.co")
      .addConverterFactory(GsonConverterFactory.create())
      .addCallAdapterFactory(CoroutineCallAdapterFactory())
      .client(
        OkHttpClient.Builder()
          .addInterceptor(interceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
          })
          .build()
      )
      .build()
      .create(PokemonsApi::class.java)
}
