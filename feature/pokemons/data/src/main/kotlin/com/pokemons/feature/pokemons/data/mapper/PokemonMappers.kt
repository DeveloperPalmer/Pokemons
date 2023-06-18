package com.pokemons.feature.pokemons.data.mapper

import com.pokemons.feature.pokemons.domain.entity.Pokemon as DomainPokemon
import com.pokemons.feature.pokemons.data.entity.Pokemon

fun Pokemon.toDomainModel(): DomainPokemon {
  return DomainPokemon(
    id = id,
    name = name,
    avatar = sprites.other.dreamWorld.avatar
  )
}
