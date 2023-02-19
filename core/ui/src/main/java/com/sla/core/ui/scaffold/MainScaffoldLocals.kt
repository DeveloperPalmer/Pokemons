package com.sla.core.ui.scaffold

import androidx.compose.runtime.staticCompositionLocalOf

val LocalMainScaffoldController = staticCompositionLocalOf<MainScaffoldController> {
  error("No MainScaffoldController provided")
}
