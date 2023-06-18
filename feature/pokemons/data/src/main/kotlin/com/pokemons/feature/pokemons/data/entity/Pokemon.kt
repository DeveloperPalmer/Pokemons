package com.pokemons.feature.pokemons.data.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Pokemon(
  @SerialName("id")
  val id: Int,
  @SerialName("name")
  val name: String,
  @SerialName("sprites")
  val sprites: Sprites
)

@Serializable
data class Sprites(
  @SerialName("other")
  val other: Other
)

@Serializable
data class Other(
  @SerialName("dream_world")
  val dreamWorld: DreamWorld
)

@Serializable
data class DreamWorld(
  @SerialName("front_default")
  val avatar: String
)
