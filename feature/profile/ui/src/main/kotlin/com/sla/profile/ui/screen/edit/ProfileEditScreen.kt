package com.sla.profile.ui.screen.edit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.sla.core.ui.R
import com.sla.core.ui.screen.MviScreen
import com.sla.core.ui.screen.OnBackPressedHandler
import com.sla.core.ui.screen.baseViewEventConfig
import com.sla.core.ui.theme.AppTheme
import com.sla.core.uikit.ButtonAppearance
import com.sla.core.uikit.ButtonSize
import com.sla.core.uikit.PrimaryButton
import com.sla.core.uikit.TopAppBar
import com.sla.core.uikit.VSpacer
import com.sla.core.uikit.WSpacer
import com.sla.core.uikit.textfield.TextField
import com.sla.feature.auth.domain.entity.AuthException
import com.sla.feature.profile.domain.entity.ProfileEditOption
import com.sla.mvi.BaseViewIntents
import com.sla.mvi.event.ViewEventPresentation
import com.sla.mvi.rememberViewIntents
import com.sla.mvi.resources.resRef
import com.sla.profile.ui.mapper.name
import com.sla.profile.ui.mapper.toMessage

@Composable
fun ProfileEditScreen(model: ProfileEditViewModel) {
  MviScreen(
    viewModel = model,
    intents = rememberViewIntents(),
    viewEventConfig = baseViewEventConfig { event ->
      when(event) {
        ViewEvent.Success -> ViewEventPresentation.Snackbar(
          message = resRef(R.string.account_created_message)
        )
        ViewEvent.AlreadyCreated -> ViewEventPresentation.Snackbar(
          message = resRef(R.string.account_already_created_message),
        )

        ViewEvent.EmailChanged -> ViewEventPresentation.Snackbar(
          message = resRef(R.string.email_changed_message),
        )
        ViewEvent.PasswordChanged -> ViewEventPresentation.Snackbar(
          message = resRef(R.string.password_changed_message),
        )
      }
    }
  ) { state, intent ->
    OnBackPressedHandler(onBack = intent.navigateBack)
    Column(
      modifier = Modifier
        .fillMaxSize()
        .systemBarsPadding()
        .padding(bottom = 16.dp)
    ) {
      TopAppBar(
        onClick = intent.navigateBack,
        title =
        when (state.profileEditOption) {
          ProfileEditOption.Email -> stringResource(R.string.edit_email)
          ProfileEditOption.Password -> stringResource(R.string.edit_password)
          null -> ""
        }
      )
      VSpacer(size = 16.dp)
      when (state.profileEditOption) {
        ProfileEditOption.Email -> {
          EditField(
            modifier = Modifier.padding(horizontal = 16.dp),
            value = state.email,
            onValueChange = intent.changeEmail,
            placeholder = stringResource(id = R.string.email),
            authException = state.authError as? AuthException.IncorrectEmailException,
            textVisible = true,
            changeTextVisibility = {},
          )
        }
        ProfileEditOption.Password -> {
          EditField(
            modifier = Modifier.padding(horizontal = 16.dp),
            value = state.oldPassword,
            onValueChange = intent.changeOldPassword,
            textVisible = state.oldPasswordVisible,
            textOptionAvailable = true,
            placeholder = stringResource(id = R.string.old_password),
            authException = state.authError as? AuthException.IncorrectPasswordException,
            changeTextVisibility = intent.changeOldPasswordVisibility
          )
          VSpacer(size = 32.dp)
          EditField(
            modifier = Modifier.padding(horizontal = 16.dp),
            value = state.newPassword,
            onValueChange = intent.changeNewPassword,
            placeholder = stringResource(id = R.string.new_password),
            textVisible = state.newPasswordVisible,
            textOptionAvailable = true,
            authException = state.authError as? AuthException.IncorrectPasswordException,
            changeTextVisibility = intent.changeNewPasswordVisibility
          )
          VSpacer(size = 16.dp)
          EditField(
            modifier = Modifier.padding(horizontal = 16.dp),
            value = state.repeatNewPassword,
            onValueChange = intent.changeRepeatNewPassword,
            placeholder = stringResource(id = R.string.repeat_new_password),
            textVisible = state.repeatNewPasswordVisible,
            textOptionAvailable = true,
            authException = state.authError as? AuthException.PasswordsMismatchException,
            changeTextVisibility = intent.changeRepeatNewPasswordVisibility
          )
        }
        null -> Unit
      }
      WSpacer()
      PrimaryButton(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
        text = stringResource(id = R.string.save),
        onClick = intent.save,
        size = ButtonSize.Large,
        appearance = ButtonAppearance.Accent
      )
    }
  }
}

class ViewIntents : BaseViewIntents() {
  val navigateBack = intent(name = "navigateBack")

  val save = intent(name = "save")

  val changeEmail = intent<String>("changeEmail")

  val changeOldPassword = intent<String>("changeOldPassword")
  val changeNewPassword = intent<String>("changeNewPassword")
  val changeRepeatNewPassword = intent<String>("changeRepeatNewPassword")

  val changeOldPasswordVisibility = intent("changeOldPasswordVisibility")
  val changeNewPasswordVisibility = intent("changeNewPasswordVisibility")
  val changeRepeatNewPasswordVisibility = intent("changeNewRepeatPasswordVisibility")
}

@Immutable
data class ViewState(
  val profileEditOption: ProfileEditOption? = null,

  val email: String = "",

  val oldPassword: String = "",
  val newPassword: String = "",
  val repeatNewPassword: String = "",

  val oldPasswordVisible: Boolean = false,
  val newPasswordVisible: Boolean = false,
  val repeatNewPasswordVisible: Boolean = false,

  val authError: AuthException? = null,
)

sealed class ViewEvent {
  object Success: ViewEvent()
  object AlreadyCreated: ViewEvent()
  object PasswordChanged: ViewEvent()
  object EmailChanged : ViewEvent()
}

@Composable
private fun EditField(
  value: String,
  onValueChange: (String) -> Unit,
  modifier: Modifier = Modifier,
  authException: AuthException? = null,
  placeholder: String? = null,
  textOptionAvailable: Boolean? = false,
  textVisible: Boolean = true,
  keyboardType: KeyboardType = KeyboardType.Text,
  changeTextVisibility: () -> Unit,
) {
  TextField(
    modifier = modifier.fillMaxWidth(),
    value = value,
    onValueChange = onValueChange,
    placeholder = placeholder,
    isError = authException != null,
    helperText = authException?.toMessage(),
    keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
    visualTransformation = if (textVisible) {
      VisualTransformation.None
    } else {
      PasswordVisualTransformation()
    },
    trailingContent = {
      if (textOptionAvailable == true) {
        Text(
          modifier = Modifier.clickable(onClick = changeTextVisibility),
          text = textVisible.name(),
          color = AppTheme.colors.grey,
          style = AppTheme.typography.caption1
        )
      }
    }
  )
}
