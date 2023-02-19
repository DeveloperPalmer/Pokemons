package com.sla.feature.pokemons.data.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PokemonSpecies(
  @SerialName("id")
  val id: Int,
  @SerialName("name")
  val name: String,
  @SerialName("flavor_text_entries")
  val flavors: List<Flavor>? = null
)

@Serializable
data class Flavor(
  @SerialName("flavor_text")
  val flavorText: String,
)
