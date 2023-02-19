package com.sla.feature.login.ui.navigation

sealed class FlowEvent {

  object LoginDismissed: FlowEvent()
  object CreateAccountRequested: FlowEvent()
  object CreateAccountDismissed: FlowEvent()
}
