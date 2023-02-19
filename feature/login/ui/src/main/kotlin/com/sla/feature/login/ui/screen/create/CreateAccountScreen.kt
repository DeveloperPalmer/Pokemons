package com.sla.feature.login.ui.screen.create

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
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
import com.sla.core.uikit.VSpacer
import com.sla.core.uikit.WSpacer
import com.sla.core.uikit.textfield.TextField
import com.sla.feature.auth.domain.entity.AuthException
import com.sla.feature.auth.domain.entity.AuthException.IncorrectEmailException
import com.sla.feature.auth.domain.entity.AuthException.IncorrectPasswordException
import com.sla.feature.auth.domain.entity.AuthException.PasswordsMismatchException
import com.sla.mvi.BaseViewIntents
import com.sla.mvi.event.ViewEventPresentation
import com.sla.mvi.rememberViewIntents
import com.sla.mvi.resources.resRef
import com.sla.core.uikit.TopAppBar
import com.sla.feature.login.ui.mapper.name
import com.sla.feature.login.ui.mapper.toMessage

@Composable
fun CreateAccountScreen(model: CreateAccountViewModel) {
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
      }
    }
  ) { state, intent ->
    OnBackPressedHandler(onBack = intent.navigateBack)
    Column(
      modifier = Modifier
        .systemBarsPadding()
        .padding(bottom = 24.dp)
    ) {
      TopAppBar(
        modifier = Modifier
          .heightIn(52.dp)
          .padding(horizontal = 4.dp),
        title = stringResource(id = R.string.create_account),
        onClick = intent.navigateBack
      )
      VSpacer(size = 20.dp)
      TextField(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
        value = state.email,
        onValueChange = intent.changeEmail,
        placeholder = stringResource(id = R.string.email),
        isError = state.authError is IncorrectEmailException,
        helperText = (state.authError as? IncorrectEmailException)?.toMessage(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
      )
      VSpacer(size = 32.dp)
      TextField(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
        value = state.password,
        onValueChange = intent.changePassword,
        placeholder = stringResource(id = R.string.password),
        isError = state.authError is IncorrectPasswordException,
        helperText = (state.authError as? IncorrectPasswordException)?.toMessage(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = if (state.passwordVisible) {
          VisualTransformation.None
        } else {
          PasswordVisualTransformation()
        },
        trailingContent = {
          Text(
            modifier = Modifier.clickable(onClick = intent.changePasswordVisibility),
            text = state.passwordVisible.name(),
            color = AppTheme.colors.grey,
            style = AppTheme.typography.caption1
          )
        }
      )
      VSpacer(size = 16.dp)
      TextField(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
        value = state.repeatPassword,
        onValueChange = intent.changeRepeatPassword,
        placeholder = stringResource(id = R.string.repeate_password),
        isError = state.authError is PasswordsMismatchException,
        helperText = (state.authError as? PasswordsMismatchException)?.toMessage(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = if (state.repeatPasswordVisible) {
          VisualTransformation.None
        } else {
          PasswordVisualTransformation()
        },
        trailingContent = {
          Text(
            modifier = Modifier.clickable(onClick = intent.changeRepeatPasswordVisibility),
            text = state.repeatPasswordVisible.name(),
            color = AppTheme.colors.grey,
            style = AppTheme.typography.caption1
          )
        }
      )
      WSpacer()
      PrimaryButton(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
        text = stringResource(id = R.string.create_account),
        onClick = intent.createAccount,
        size = ButtonSize.Large,
        appearance = ButtonAppearance.Accent
      )
    }
  }
}

class ViewIntents : BaseViewIntents() {
  val navigateBack = intent("navigateBack")
  val changeEmail = intent<String>("changeEmail")
  val changePassword = intent<String>("changePassword")
  val changeRepeatPassword = intent<String>("changeRepeatPassword")
  val createAccount = intent("createAccount")
  val changePasswordVisibility = intent("changePasswordVisibility")
  val changeRepeatPasswordVisibility = intent("changeRepeatPasswordVisibility")
}

@Immutable
data class ViewState(
  val email: String = "",
  val password: String = "",
  val repeatPassword: String = "",
  val passwordVisible: Boolean = false,
  val repeatPasswordVisible: Boolean = false,
  val authError: AuthException? = null,
)

sealed class ViewEvent {
  object Success: ViewEvent()
  object AlreadyCreated: ViewEvent()
}
