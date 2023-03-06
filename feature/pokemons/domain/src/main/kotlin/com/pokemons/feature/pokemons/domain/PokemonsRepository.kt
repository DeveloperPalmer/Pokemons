package com.pokemons.feature.pokemons.domain

import com.pokemons.feature.pokemons.domain.entity.Pokemon

interface PokemonsRepository {

  suspend fun getPokemons(page: Int): List<Pokemon>
}