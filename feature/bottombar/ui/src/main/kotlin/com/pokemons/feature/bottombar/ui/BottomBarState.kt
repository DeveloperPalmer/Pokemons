package com.pokemons.feature.bottombar.ui

import androidx.compose.runtime.Immutable

@Immutable
data class BottomBarState(
  val selectedSection: BottomBarSection? = null,
  val notifications: Map<BottomBarSection, Notification?> = emptyMap(),
)

enum class BottomBarSection {
  Pokemons
}

sealed class Notification {
  object Alert : Notification()
  data class Count(val count: Int) : Notification()
}
