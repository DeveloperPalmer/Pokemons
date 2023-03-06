package com.pokemons.feature.pokemons.domain

import com.pokemons.feature.core.domain.di.SingleIn
import com.pokemons.feature.pokemons.domain.di.PokemonsScope
import javax.inject.Inject

@SingleIn(PokemonsScope::class)
class PokemonsModel @Inject constructor(
  private val pokemonsRepository: PokemonsRepository
) {

}