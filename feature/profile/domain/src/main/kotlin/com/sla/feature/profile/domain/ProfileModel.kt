package com.sla.feature.profile.domain

import com.sla.feature.auth.domain.AuthSessionRepository
import com.sla.feature.auth.domain.entity.Passwords
import com.sla.feature.core.domain.di.SingleIn
import com.sla.feature.core.domain.model.ReactiveModel
import com.sla.feature.profile.domain.di.ProfileScope
import com.sla.feature.profile.domain.entity.Profile
import com.sla.feature.profile.domain.entity.ProfileEditOption
import com.sla.feature.profile.domain.mapper.toProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@SingleIn(ProfileScope::class)
class ProfileModel @Inject constructor(
  private val authSessionRepository: AuthSessionRepository
) : ReactiveModel() {

  private val stateFlow = MutableStateFlow(State())

  val changeEmail = task<String, Unit> { email ->
    authSessionRepository.changeEmail(email)
  }

  val changePassword = task<Passwords, Unit> { updatePassword ->
    authSessionRepository.changePassword(updatePassword)
  }

  fun logout() {
    scope.launch { authSessionRepository.logout() }
  }

  fun setProfileEditOption(option: ProfileEditOption) {
    stateFlow.update { it.copy(profileEditOption = option) }
  }

  val profile: Flow<Profile> = authSessionRepository.account().mapNotNull { it?.toProfile() }
  val profileEditOption: Flow<ProfileEditOption> = stateFlow.mapNotNull { it.profileEditOption }

  private data class State(
    val profileEditOption: ProfileEditOption? = null
  )
}
