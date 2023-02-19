package com.sla.feature.auth.domain.entity

import android.util.Patterns

data class SignUp(
  val email: String,
  val password: String,
  val repeatedPassword: String,
) {

  fun validate() {
    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
      throw AuthException.IncorrectEmailException
    }
    if (password.length < PASSWORD_MIN_LENGTH) {
      throw AuthException.IncorrectPasswordException
    }
    if (password != repeatedPassword) {
      throw AuthException.PasswordsMismatchException
    }
  }
}
