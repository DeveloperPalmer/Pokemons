package com.sla.feature.login.ui.screen.login

import com.sla.core.content.ContentLoadState
import com.sla.core.content.configureContentAction
import com.sla.feature.auth.domain.entity.LoginIn
import com.sla.feature.login.domain.LoginModel
import com.sla.mvi.BaseViewModel
import com.sla.feature.login.ui.navigation.FlowEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import ru.dimsuz.unicorn.coroutines.MachineDsl
import javax.inject.Inject

class LoginViewModel @Inject constructor(
  private val loginModel: LoginModel,
  private val flowEvents: MutableSharedFlow<FlowEvent>
) : BaseViewModel<ViewState, ViewIntents, ViewEvent, Unit>() {

  override fun MachineDsl<ViewState, Unit>.buildMachine() {
    initial = ViewState() to null

    configureEmailTransition()
    configurePasswordTransition()

    onEach(intent(ViewIntents::navigateBack)) {
      action { _, _, _ ->
        flowEvents.tryEmit(FlowEvent.LoginDismissed)
      }
    }

    onEach(typedIntent(ViewIntents::changeEmail)) {
      transitionTo { state, query ->
        state.copy(email = query)
      }
    }
    onEach(intent(ViewIntents::createAccount)) {
      action { _, _, _ ->
        flowEvents.tryEmit(FlowEvent.CreateAccountRequested)
      }
    }

    onEach(intent(ViewIntents::signIn)) {
      action { state, _, _ ->
        loginModel.loginIn.start(LoginIn(email = state.email, password = state.password))
      }
    }

    configureContentAction(
      task = loginModel.loginIn,
      contentStateRead = { ContentLoadState.Ready },
      loadingStateWrite = { state, _ -> state },
      onSuccess = { _, _, _ -> flowEvents.tryEmit(FlowEvent.LoginDismissed) },
      onError = { _, _ -> sendViewEvent(ViewEvent.InvalidAccount) }
    )
  }

  private fun MachineDsl<ViewState, Unit>.configureEmailTransition() {
    onEach(typedIntent(ViewIntents::changeEmail)) {
      transitionTo { state, query ->
        state.copy(email = query,)
      }
    }
  }

  private fun MachineDsl<ViewState, Unit>.configurePasswordTransition() {
    onEach(typedIntent(ViewIntents::changePassword)) {
      transitionTo { state, query ->
        state.copy(password = query,)
      }
    }
    onEach(intent(ViewIntents::changePasswordVisibility)) {
      transitionTo { state, _ ->
        state.copy(passwordVisible = !state.passwordVisible)
      }
    }
  }
}
