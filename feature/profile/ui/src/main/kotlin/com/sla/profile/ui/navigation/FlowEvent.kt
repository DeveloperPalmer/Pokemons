package com.sla.profile.ui.navigation

sealed class FlowEvent {

  object ProfileEditRequested: FlowEvent()
  object ProfileEditDismissed: FlowEvent()
  object ProfileDismissed: FlowEvent()
}

