package ru.kode.mvi.screen.transition

import com.sla.mvi.screen.transition.TransitionType

data class ScreenTransition(
  val transitionType: TransitionType = TransitionType.RightToLeft,
)
