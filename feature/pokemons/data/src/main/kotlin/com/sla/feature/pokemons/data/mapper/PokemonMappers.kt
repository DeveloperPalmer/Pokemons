package com.sla.feature.pokemons.data.mapper

import com.sla.feature.pokemons.domain.entity.Pokemon as DomainPokemon
import com.sla.feature.pokemons.domain.entity.PokemonSpecies as DomainPokemonAbilities
import com.sla.feature.pokemons.data.entity.Pokemon
import com.sla.feature.pokemons.data.entity.PokemonSpecies

fun Pokemon.toDomainModel(): DomainPokemon {
  return DomainPokemon(
    id = id,
    name = name,
    avatar = sprites.other.dreamWorld.avatar,
    isFavorites = false
  )
}

fun PokemonSpecies.toDomainModel(): DomainPokemonAbilities {
  val abilities = this
  return DomainPokemonAbilities(
    effects = buildString {
      abilities.flavors?.forEach { flavor -> append(flavor.flavorText) }
    }
  )
}
