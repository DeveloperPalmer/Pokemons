package com.sla.core.content

import com.sla.core.ui.R
import com.sla.mvi.resources.resRef

/**
 * Converts a commonly known domain error into a ui error.
 * If an error is not known and unexpected, it is converted to a generic error using [fallbackMapper] function
 */
fun Throwable.toAppUiError(
  action: UiMessage.Action? = null,
  fallbackMapper: (Throwable, UiMessage.Action?) -> UiError = Throwable::toGenericUiError,
): UiError {
  return fallbackMapper(this, action)
}

fun Throwable.toGenericUiError(
  action: UiMessage.Action?,
): UiError {
  return UiError(
    message = UiMessage(
      title = resRef(id = R.string.something_is_not_working),
      description = resRef(id = R.string.error_system_message),
      action = action,
    ),
    cause = this,
  )
}
