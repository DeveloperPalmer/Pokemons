package com.sla.feature.auth.domain.entity

data class Passwords(
  val oldPass: String,
  val newPass: String,
  val repeatNewPass: String
) {

  fun validate() {
    if (oldPass == newPass) {
      throw AuthException.IncorrectPasswordException
    }
    if (oldPass.length < PASSWORD_MIN_LENGTH || newPass.length < PASSWORD_MIN_LENGTH) {
      throw AuthException.IncorrectPasswordException
    }
    if (newPass != repeatNewPass) {
      throw AuthException.PasswordsMismatchException
    }
  }
}
