package com.sla.feature.auth.domain.entity

sealed class AuthException: RuntimeException() {
  object AccountNotFound: AuthException()
  object AccountAlreadyExistException: AuthException()
  object IncorrectEmailException: AuthException()
  object IncorrectPasswordException: AuthException()
  object PasswordsMismatchException: AuthException()
}

const val PASSWORD_MIN_LENGTH = 4
