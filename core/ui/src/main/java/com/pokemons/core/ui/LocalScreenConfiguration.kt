package com.pokemons.core.ui

import androidx.compose.runtime.staticCompositionLocalOf
import com.pokemons.feature.core.domain.entity.ScreenConfiguration

val LocalScreenConfiguration = staticCompositionLocalOf<ScreenConfiguration> {
  error("No ScreenConfiguration provided")
}

