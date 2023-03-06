package com.pokemons.feature.core.domain.entity

import androidx.compose.runtime.Immutable

@Immutable
data class ScreenConfiguration(
  val orientation: ScreenOrientation,
  val rotation: ScreenRotation
)
