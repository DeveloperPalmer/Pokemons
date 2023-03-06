package com.pokemons.core.ui.scaffold

import androidx.compose.runtime.staticCompositionLocalOf
import com.pokemons.core.ui.scaffold.MainScaffoldController

val LocalMainScaffoldController = staticCompositionLocalOf<MainScaffoldController> {
  error("No MainScaffoldController provided")
}
