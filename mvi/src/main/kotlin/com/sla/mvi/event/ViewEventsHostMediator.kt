package com.sla.mvi.event

import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class ViewEventsHostMediator {

  private val _events = MutableSharedFlow<ViewEventPresentation>(extraBufferCapacity = 3)
  val events: Flow<ViewEventPresentation> = _events

  fun sendViewEvent(event: ViewEventPresentation) {
    _events.tryEmit(event)
  }
}

val LocalViewEventsHostMediator = staticCompositionLocalOf<ViewEventsHostMediator> {
  error("No ViewEventsHostMediator provided")
}
