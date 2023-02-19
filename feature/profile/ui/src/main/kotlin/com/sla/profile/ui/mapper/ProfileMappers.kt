package com.sla.profile.ui.mapper

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sla.core.ui.R
import com.sla.feature.auth.domain.entity.AuthException

@Composable
fun Boolean.name(): String {
  return stringResource(if (this) R.string.hide else R.string.show)
}

@Composable
fun AuthException.toMessage(): String? {
  return when(this) {
    AuthException.IncorrectEmailException -> stringResource(id = R.string.email_error)
    AuthException.IncorrectPasswordException -> stringResource(id = R.string.password_error)
    AuthException.PasswordsMismatchException -> stringResource(id = R.string.password_mismatch_error)
    else -> null
  }
}
