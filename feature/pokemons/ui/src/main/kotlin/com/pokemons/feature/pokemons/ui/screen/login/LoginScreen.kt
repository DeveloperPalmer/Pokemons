package com.pokemons.feature.pokemons.ui.screen.login

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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.pokemons.core.ui.screen.MviScreen
import com.pokemons.core.ui.screen.OnBackPressedHandler
import com.pokemons.core.ui.theme.AppTheme
import com.pokemons.core.uikit.PrimaryButton
import com.pokemons.core.uikit.SecondaryButton
import com.pokemons.core.uikit.VSpacer
import com.pokemons.core.uikit.WSpacer
import com.pokemons.core.uikit.textfield.TextField
import com.pokemons.mvi.BaseViewIntents
import com.pokemons.mvi.rememberViewIntents

@Composable
fun LoginScreen(model: LoginViewModel) {
  MviScreen(
    viewModel = model,
    intents = rememberViewIntents(),
  ) { state, intent ->
    OnBackPressedHandler(onBack = intent.navigateBack)
    Column(
      modifier = Modifier
        .systemBarsPadding()
        .padding(top = 56.dp, bottom = 24.dp, start = 16.dp, end = 16.dp)
    ) {
      Text(
        text = state.title,
        color = AppTheme.colors.textPrimary,
        style = AppTheme.typography.h1
      )
      VSpacer(size = 8.dp)
      Text(
        text = state.subtitle,
        color = AppTheme.colors.textPrimary,
        style = AppTheme.typography.body
      )
      WSpacer()
      TextField(
        modifier = Modifier.fillMaxWidth(),
        value = state.email,
        onValueChange = intent.changeEmail,
        placeholder = state.emailHint,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
      )
      VSpacer(size = 32.dp)
      TextField(
        modifier = Modifier.fillMaxWidth(),
        value = state.password,
        onValueChange = intent.changePassword,
        placeholder = state.passwordHint,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = when(state.passwordSecurity) {
          PasswordSecurity.SHOW -> PasswordVisualTransformation()
          PasswordSecurity.HIDE -> VisualTransformation.None
        },
        trailingContent = {
          Text(
            modifier = Modifier.clickable(onClick = intent.showPassword),
            text = state.passwordSecurity.name,
            color = AppTheme.colors.grey,
            style = AppTheme.typography.caption1
          )
        }
      )
      WSpacer()
      PrimaryButton(
        modifier = Modifier.fillMaxWidth(),
        text = state.signIn,
        onClick = intent.signIn
      )
      VSpacer(size = 16.dp)
      SecondaryButton(
        modifier = Modifier.fillMaxWidth(),
        text = state.createAccount,
        onClick = intent.createAccount
      )
    }
  }
}

class ViewIntents : BaseViewIntents() {
  val navigateBack = intent("navigateBack")
  val changeEmail = intent<String>("changeEmail")
  val changePassword = intent<String>("changePassword")
  val signIn = intent("signIn")
  val createAccount = intent("createAccount")
  val showPassword= intent("showPassword")
}

@Immutable
data class ViewState(
  val title: String = "Hello!",
  val subtitle: String = "Please log in to the system to start working",
  val bookingUrlPath: String? = null,
  val email: String = "",
  val emailHint: String = "Email",
  val passwordHint: String = "Password",
  val password: String = "",
  val signIn: String = "Sign In",
  val createAccount: String = "Create Account",
  val passwordSecurity: PasswordSecurity = PasswordSecurity.SHOW
)

enum class PasswordSecurity {
  SHOW, HIDE
}
