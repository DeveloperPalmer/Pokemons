package com.pokemons.feature.pokemons.data

import com.pokemons.feature.core.domain.di.SingleIn
import com.pokemons.feature.pokemons.data.api.PokemonsApi
import com.pokemons.feature.pokemons.domain.PokemonsRepository
import com.pokemons.feature.pokemons.domain.di.PokemonsScope
import com.pokemons.feature.pokemons.domain.entity.Pokemon
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.*
import javax.inject.Inject

@SingleIn(PokemonsScope::class)
@ContributesBinding(PokemonsScope::class)
class PokemonsDataRepository @Inject constructor(
  private val pokemonsApi: PokemonsApi
): PokemonsRepository {

  private val coroutineScope = CoroutineScope(Dispatchers.IO)

  override suspend fun getPokemons(page: Int): List<Pokemon> {
    val count = 20
    val pokemonStartIndex = page * count
    val pokemons = mutableListOf<Pokemon>()
    repeat(count) {
      coroutineScope
        .launch {
          val item: Pokemon = pokemonsApi.requestPokemonById(id = pokemonStartIndex)
          pokemons.add(item)
        }
        .join()
    }
    coroutineScope.cancel()
    return pokemons
  }
}