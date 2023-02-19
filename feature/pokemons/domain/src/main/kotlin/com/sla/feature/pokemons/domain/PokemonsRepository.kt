package com.sla.feature.pokemons.domain

import com.sla.feature.pokemons.domain.entity.Pokemon
import com.sla.feature.pokemons.domain.entity.PokemonSpecies

interface PokemonsRepository {
  suspend fun getPokemons(page: Int): List<Pokemon>
  suspend fun getPokemon(id: Int): Pokemon
  suspend fun getPokemonAbilities(name: String): PokemonSpecies
}
