package com.sla.feature.core.domain.entity

enum class ScreenRotation(val angleRadians: Double) {
  PortraitUp(angleRadians = 0.0),
  LandscapeRight(angleRadians = Math.PI / 2.0),
  PortraitDown(angleRadians = Math.PI),
  LandscapeLeft(angleRadians = 3.0 * Math.PI / 2.0),
}
