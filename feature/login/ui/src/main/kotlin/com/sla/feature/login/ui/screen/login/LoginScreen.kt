package com.sla.feature.login.ui.screen.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
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
import com.sla.core.uikit.SecondaryButton
import com.sla.core.uikit.VSpacer
import com.sla.core.uikit.WSpacer
import com.sla.core.uikit.textfield.TextField
import com.sla.mvi.BaseViewIntents
import com.sla.mvi.event.ViewEventPresentation
import com.sla.mvi.rememberViewIntents
import com.sla.mvi.resources.resRef
import com.sla.feature.login.ui.mapper.name

@Composable
fun LoginScreen(model: LoginViewModel) {
  MviScreen(
    viewModel = model,
    intents = rememberViewIntents(),
    viewEventConfig = baseViewEventConfig { event ->
      when(event) {
        ViewEvent.InvalidAccount -> ViewEventPresentation.Snackbar(
          message = resRef(R.string.invalid_account)
        )
      }
    }
  ) { state, intent ->
    OnBackPressedHandler(onBack = intent.navigateBack)
    Column(
      modifier = Modifier
        .systemBarsPadding()
        .padding(top = 56.dp, bottom = 24.dp, start = 16.dp, end = 16.dp)
    ) {
      Text(
        text = stringResource(id = R.string.hello),
        color = AppTheme.colors.textPrimary,
        style = AppTheme.typography.h1
      )
      VSpacer(size = 8.dp)
      Text(
        text = stringResource(id = R.string.login_to_start_working),
        color = AppTheme.colors.textPrimary,
        style = AppTheme.typography.body
      )
      WSpacer()
      TextField(
        modifier = Modifier.fillMaxWidth(),
        value = state.email,
        onValueChange = intent.changeEmail,
        placeholder = stringResource(id = R.string.email),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
      )
      VSpacer(size = 32.dp)
      TextField(
        modifier = Modifier.fillMaxWidth(),
        value = state.password,
        onValueChange = intent.changePassword,
        placeholder = stringResource(id = R.string.password),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = if(state.passwordVisible) {
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
      WSpacer()
      PrimaryButton(
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(id = R.string.sign_in),
        onClick = intent.signIn,
        size = ButtonSize.Large,
        appearance = ButtonAppearance.Accent
      )
      VSpacer(size = 16.dp)
      SecondaryButton(
        modifier = Modifier.fillMaxWidth(),
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
  val changePasswordVisibility= intent("changePasswordVisibility")
  val signIn = intent("signIn")
  val createAccount = intent("createAccount")
}

@Immutable
data class ViewState(
  val bookingUrlPath: String? = null,
  val email: String = "",
  val password: String = "",
  val passwordVisible: Boolean = false,
)

sealed class ViewEvent {
  object InvalidAccount : ViewEvent()
}
