package com.sla.profile.ui.screen.profile

import com.sla.feature.profile.domain.ProfileModel
import com.sla.feature.profile.domain.entity.ProfileEditOption
import com.sla.mvi.BaseViewModel
import com.sla.profile.ui.navigation.FlowEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import ru.dimsuz.unicorn.coroutines.MachineDsl
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
  private val profileModel: ProfileModel,
  private val flowEvents: MutableSharedFlow<FlowEvent>
) : BaseViewModel<ViewState, ViewIntents, Unit, Unit>() {

  override fun MachineDsl<ViewState, Unit>.buildMachine() {
    initial = ViewState() to null

    onEach(intent(ViewIntents::navigateBack)) {
      action { _, _, _ ->
        flowEvents.tryEmit(FlowEvent.ProfileDismissed)
      }
    }

    onEach(profileModel.profile) {
      transitionTo { state, profile ->
        state.copy(email = profile.email)
      }
    }

    onEach(intent(ViewIntents::changeEmail)) {
      action { _, _, _ ->
        profileModel.setProfileEditOption(ProfileEditOption.Email)
        flowEvents.tryEmit(FlowEvent.ProfileEditRequested)
      }
    }

    onEach(intent(ViewIntents::changePassword)) {
      action { _, _, _ ->
        profileModel.setProfileEditOption(ProfileEditOption.Password)
        flowEvents.tryEmit(FlowEvent.ProfileEditRequested)
      }
    }

    onEach(intent(ViewIntents::logout)) {
      action { _, _, _ ->
        profileModel.logout()
        flowEvents.tryEmit(FlowEvent.ProfileDismissed)
      }
    }
  }
}

