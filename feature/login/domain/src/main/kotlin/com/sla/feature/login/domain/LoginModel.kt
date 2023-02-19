package com.sla.feature.login.domain

import android.util.Log
import com.sla.feature.auth.domain.AuthSessionRepository
import com.sla.feature.auth.domain.entity.LoginIn
import com.sla.feature.auth.domain.entity.SignUp
import com.sla.feature.core.domain.di.SingleIn
import com.sla.feature.core.domain.model.ReactiveModel
import com.sla.feature.login.domain.di.LoginScope
import javax.inject.Inject

@SingleIn(LoginScope::class)
class LoginModel @Inject constructor(
  private val authRepository: AuthSessionRepository,
) : ReactiveModel() {

  val loginIn = task<LoginIn, Unit> { loginIn ->
    val accountId = authRepository.findAccountId(loginIn.email, loginIn.password)
    authRepository.saveAccountId(accountId)
  }

  val createAccount = task<SignUp, Unit> { signUp ->
    signUp.validate()
    authRepository.createAccount(signUp)
  }
}
