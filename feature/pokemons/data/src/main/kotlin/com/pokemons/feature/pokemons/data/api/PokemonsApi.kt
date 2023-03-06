package com.pokemons.feature.pokemons.data.api

import com.pokemons.feature.pokemons.domain.entity.Pokemon
import retrofit2.http.GET
import retrofit2.http.Path

interface PokemonsApi {

  @GET("/api/v2/pokemon/{id}")
  suspend fun requestPokemonById(@Path("id") id: Int): Pokemon
}