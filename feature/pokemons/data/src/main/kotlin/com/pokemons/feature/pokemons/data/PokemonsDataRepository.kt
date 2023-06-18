package com.pokemons.feature.pokemons.data

import com.pokemons.feature.core.domain.di.SingleIn
import com.pokemons.feature.pokemons.data.entity.Pokemon
import com.pokemons.feature.pokemons.data.mapper.toDomainModel
import com.pokemons.feature.pokemons.domain.PokemonsRepository
import com.sla.feature.app.AppScope
import com.squareup.anvil.annotations.ContributesBinding
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.url
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.pokemons.feature.pokemons.domain.entity.Pokemon as DomainPokemon

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class PokemonsDataRepository @Inject constructor(
  private val client: HttpClient
) : PokemonsRepository {

  private val coroutineScope = CoroutineScope(Dispatchers.IO)

  override suspend fun getPokemons(page: Int): List<DomainPokemon> {
    val count = 20
    val pokemonStartIndex = page * count
    val pokemons = mutableListOf<DomainPokemon>()
    repeat(count) { index ->
      val pokemon = getPokemon(pokemonStartIndex + index)
      coroutineScope.launch { pokemons.add(pokemon) }.join()
    }
    coroutineScope.cancel()
    return pokemons
  }

  override suspend fun getPokemon(id: Int): DomainPokemon {
    val response = client.get { url("$BASE_URL$id") }
    val pokemon = response.body<Pokemon>()
    return pokemon.toDomainModel()
  }
}

private const val BASE_URL = "https://pokeapi.co/api/v2/pokemon/"
