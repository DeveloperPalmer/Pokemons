package com.sla.feature.login.ui.screen.create

import com.sla.core.content.ContentLoadState
import com.sla.core.content.configureContentAction
import com.sla.feature.auth.domain.entity.AuthException
import com.sla.feature.auth.domain.entity.SignUp
import com.sla.feature.login.domain.LoginModel
import com.sla.feature.login.ui.navigation.FlowEvent
import com.sla.mvi.BaseViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import ru.dimsuz.unicorn.coroutines.MachineDsl
import javax.inject.Inject

class CreateAccountViewModel @Inject constructor(
  private val loginModel: LoginModel,
  private val flowEvents: MutableSharedFlow<FlowEvent>
) : BaseViewModel<ViewState, ViewIntents, ViewEvent, Unit>() {

  override fun MachineDsl<ViewState, Unit>.buildMachine() {
    initial = ViewState() to null

    configureEmailTransition()
    configurePasswordTransition()
    configureRepeatedPasswordTransition()

    onEach(intent(ViewIntents::navigateBack)) {
      action { _, _, _ ->
        flowEvents.tryEmit(FlowEvent.CreateAccountDismissed)
      }
    }

    onEach(intent(ViewIntents::createAccount)) {
      action { state, _, _ ->
        loginModel.createAccount.start(
          SignUp(
            email = state.email,
            password = state.password,
            repeatedPassword = state.repeatPassword
          )
        )
      }
    }

    configureContentAction(
      task = loginModel.createAccount,
      contentStateRead = { ContentLoadState.Ready },
      loadingStateWrite = { state, _ -> state },
      errorStateWrite = { state, error ->
        state.copy(authError = error?.cause as? AuthException)
      },
      onError = { _, error ->
        if (error.cause is AuthException.AccountAlreadyExistException) {
          sendViewEvent(ViewEvent.AlreadyCreated)
        }
      },
      onSuccess = { _, _, _ ->
        sendViewEvent(ViewEvent.Success)
        flowEvents.tryEmit(FlowEvent.CreateAccountDismissed)
      }
    )
  }

  private fun MachineDsl<ViewState, Unit>.configureEmailTransition() {
    onEach(typedIntent(ViewIntents::changeEmail)) {
      transitionTo { state, query ->
        state.copy(email = query)
      }
    }
  }

  private fun MachineDsl<ViewState, Unit>.configurePasswordTransition() {
    onEach(typedIntent(ViewIntents::changePassword)) {
      transitionTo { state, query ->
        state.copy(password = query)
      }
    }
    onEach(intent(ViewIntents::changePasswordVisibility)) {
      transitionTo { state, _ ->
        state.copy(passwordVisible = !state.passwordVisible)
      }
    }
  }

  private fun MachineDsl<ViewState, Unit>.configureRepeatedPasswordTransition() {
    onEach(typedIntent(ViewIntents::changeRepeatPassword)) {
      transitionTo { state, query ->
        state.copy(repeatPassword = query)
      }
    }
    onEach(intent(ViewIntents::changeRepeatPasswordVisibility)) {
      transitionTo { state, _ ->
        state.copy(repeatPasswordVisible = !state.repeatPasswordVisible)
      }
    }
  }
}
