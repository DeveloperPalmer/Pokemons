package com.sla.profile.ui.screen.edit

import com.sla.core.content.ContentLoadState
import com.sla.core.content.configureContentAction
import com.sla.feature.auth.domain.entity.AuthException
import com.sla.feature.auth.domain.entity.Passwords
import com.sla.feature.profile.domain.ProfileModel
import com.sla.feature.profile.domain.entity.ProfileEditOption
import com.sla.mvi.BaseViewModel
import com.sla.profile.ui.navigation.FlowEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import ru.dimsuz.unicorn.coroutines.MachineDsl
import javax.inject.Inject

class ProfileEditViewModel @Inject constructor(
  private val profileModel: ProfileModel,
  private val flowEvents: MutableSharedFlow<FlowEvent>
) : BaseViewModel<ViewState, ViewIntents, ViewEvent, Unit>() {

  override fun MachineDsl<ViewState, Unit>.buildMachine() {
    initial = ViewState() to null

    onEach(intent(ViewIntents::navigateBack)) {
      action { _, _, _ ->
        flowEvents.tryEmit(FlowEvent.ProfileEditDismissed)
      }
    }

    onEach(profileModel.profileEditOption) {
      transitionTo { state, option ->
        state.copy(profileEditOption = option)
      }
    }

    onEach(intent(ViewIntents::save)) {
      action { state, _, _ ->
        when(state.profileEditOption) {
          ProfileEditOption.Email -> {
            profileModel.changeEmail.start(state.email)
          }
          ProfileEditOption.Password -> {
            profileModel.changePassword.start(
              Passwords(state.oldPassword, state.newPassword, state.repeatNewPassword)
            )
          }
          null -> Unit
        }
      }
    }

    configureContentAction(
      task = profileModel.changeEmail,
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
        flowEvents.tryEmit(FlowEvent.ProfileEditDismissed)
      }
    )

    configureContentAction(
      task = profileModel.changePassword,
      contentStateRead = { ContentLoadState.Ready },
      loadingStateWrite = { state, _ -> state },
      errorStateWrite = { state, error ->
        state.copy(authError = error?.cause as? AuthException)
      },
      onSuccess = { _, _, _ ->
        sendViewEvent(ViewEvent.Success)
        flowEvents.tryEmit(FlowEvent.ProfileEditDismissed)
      }
    )


    configureEmailTransitions()
    configureOldPasswordTransitions()
    configureNewPasswordTransitions()
    configureNewRepeatedPasswordTransitions()
  }

  private fun MachineDsl<ViewState, Unit>.configureEmailTransitions() {
    onEach(typedIntent(ViewIntents::changeEmail)) {
      transitionTo { state, query ->
        state.copy(email = query)
      }
    }
  }

  private fun MachineDsl<ViewState, Unit>.configureOldPasswordTransitions() {
    onEach(typedIntent(ViewIntents::changeOldPassword)) {
      transitionTo { state, query ->
        state.copy(oldPassword = query)
      }
    }
    onEach(intent(ViewIntents::changeOldPasswordVisibility)) {
      transitionTo { state, _ ->
        state.copy(oldPasswordVisible = !state.oldPasswordVisible)
      }
    }
  }

  private fun MachineDsl<ViewState, Unit>.configureNewPasswordTransitions() {
    onEach(typedIntent(ViewIntents::changeNewPassword)) {
      transitionTo { state, query ->
        state.copy(newPassword = query)
      }
    }
    onEach(intent(ViewIntents::changeNewPasswordVisibility)) {
      transitionTo { state, _ ->
        state.copy(newPasswordVisible = !state.newPasswordVisible)
      }
    }
  }

  private fun MachineDsl<ViewState, Unit>.configureNewRepeatedPasswordTransitions() {
    onEach(typedIntent(ViewIntents::changeRepeatNewPassword)) {
      transitionTo { state, query ->
        state.copy(repeatNewPassword = query)
      }
    }
    onEach(intent(ViewIntents::changeRepeatNewPasswordVisibility)) {
      transitionTo { state, _ ->
        state.copy(repeatNewPasswordVisible = !state.repeatNewPasswordVisible)
      }
    }
  }
}
