package com.sla.core.ui

import androidx.compose.runtime.staticCompositionLocalOf
import com.sla.feature.core.domain.entity.ScreenConfiguration

val LocalScreenConfiguration = staticCompositionLocalOf<ScreenConfiguration> {
  error("No ScreenConfiguration provided")
}

