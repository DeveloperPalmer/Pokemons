package com.pokemons.feature.pokemons.domain

import com.pokemons.feature.core.domain.di.SingleIn
import com.pokemons.feature.core.domain.model.ReactiveModel
import com.pokemons.feature.pokemons.domain.di.PokemonsScope
import com.pokemons.feature.pokemons.domain.entity.Pokemon
import kotlinx.coroutines.cancel
import javax.inject.Inject

@SingleIn(PokemonsScope::class)
class PokemonsModel @Inject constructor(
  private val pokemonsRepository: PokemonsRepository
): ReactiveModel() {

  val pokemons = task<Int, List<Pokemon>> { pages ->
    pokemonsRepository.getPokemons(pages)
  }

  fun cancel() { scope.cancel() }
}