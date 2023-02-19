package com.sla.feature.pokemons.data

import com.sla.feature.app.domain.AppScope
import com.sla.feature.core.domain.di.SingleIn
import com.sla.feature.pokemons.data.entity.Pokemon
import com.sla.feature.pokemons.data.entity.PokemonSpecies
import com.sla.feature.pokemons.data.mapper.toDomainModel
import com.sla.feature.pokemons.domain.PokemonsRepository
import com.squareup.anvil.annotations.ContributesBinding
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.url
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import com.sla.feature.pokemons.domain.entity.Pokemon as DomainPokemon
import com.sla.feature.pokemons.domain.entity.PokemonSpecies as DomainPokemonAbilities

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
    val url = "$BASE_URL$POKEMON_PATH$id"
    Timber.d("requestUrl = $url")
    val response = client.get { url("$BASE_URL$POKEMON_PATH$id") }
    val pokemon = response.body<Pokemon>()
    return pokemon.toDomainModel()
  }

  override suspend fun getPokemonAbilities(name: String): DomainPokemonAbilities {
    val url = "$BASE_URL$POKEMON_ABILITIES_PATH$name"
    Timber.d("requestUrl = $url")
    val response = client.get { url(url) }
    val pokemon = response.body<PokemonSpecies>()
    return pokemon.toDomainModel()
  }
}

private const val BASE_URL = "https://pokeapi.co/api/v2"

private const val POKEMON_PATH = "/pokemon/"
private const val POKEMON_ABILITIES_PATH = "/pokemon-species/"
