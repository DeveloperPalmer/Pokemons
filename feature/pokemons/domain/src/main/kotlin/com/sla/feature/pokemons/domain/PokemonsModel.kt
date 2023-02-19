package com.sla.feature.pokemons.domain

import com.sla.feature.auth.domain.AuthSessionRepository
import com.sla.feature.core.domain.di.SingleIn
import com.sla.feature.core.domain.mapDistinctNotNullChanges
import com.sla.feature.core.domain.model.ReactiveModel
import com.sla.feature.pokemons.domain.di.PokemonsScope
import com.sla.feature.pokemons.domain.entity.Pokemon
import com.sla.feature.pokemons.domain.entity.PokemonSpecies
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@SingleIn(PokemonsScope::class)
class PokemonsModel @Inject constructor(
  private val authRepository: AuthSessionRepository,
  private val repository: PokemonsRepository
) : ReactiveModel() {

  private val _state = MutableStateFlow(State())

  val isAuthorized = authRepository.account().map { it != null }

  val pokemons: Flow<List<Pokemon>> = _state.map { it.pokemons }

  val pokemonSpecies: Flow<PokemonSpecies> = _state
    .mapDistinctNotNullChanges { it.pokemonSpecies }

  val activePokemon: Flow<Pokemon> = _state
    .mapDistinctNotNullChanges { it.activePokemon }

  val requestPokemons = task<Int, Unit> { pages ->
    _state.update { it.copy(pokemons = repository.getPokemons(pages)) }
  }

  val requestPokemonAbilities = task<String, Unit> { name ->
    _state.update { state ->
      state.copy(
        activePokemon = state.pokemons.firstOrNull { it.name == name },
        pokemonSpecies = repository.getPokemonAbilities(name)
      )
    }
  }

  fun switchFavorites(name: String) {
    _state.update { state ->
      val pokemon = state.pokemons.first { it.name == name }
      val pokemonPosition = state.pokemons.indexOf(pokemon)
      val updatedPokemons = state.pokemons.toMutableList().apply {
        removeAt(pokemonPosition)
        add(pokemonPosition, pokemon.copy(isFavorites = !pokemon.isFavorites))
      }
      state.copy(
        activePokemon = pokemon.copy(isFavorites = !pokemon.isFavorites),
        pokemons = updatedPokemons
      )
    }
  }

  fun clearActivePokemon() {
    _state.update { it.copy(activePokemon = null, pokemonSpecies = null) }
  }
  
  private data class State(
    val pokemons: List<Pokemon> = listOf(),
    val activePokemon: Pokemon? = null,
    val pokemonSpecies: PokemonSpecies? = null
  )
}
