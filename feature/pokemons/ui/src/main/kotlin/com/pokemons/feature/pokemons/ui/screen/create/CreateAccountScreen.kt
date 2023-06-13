package com.pokemons.feature.pokemons.ui.screen.create

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.pokemons.core.ui.R
import com.pokemons.core.ui.screen.MviScreen
import com.pokemons.core.ui.theme.AppTheme
import com.pokemons.core.uikit.PrimaryButton
import com.pokemons.core.uikit.VSpacer
import com.pokemons.core.uikit.WSpacer
import com.pokemons.core.uikit.textfield.TextField
import com.pokemons.feature.pokemons.ui.screen.login.PasswordSecurity
import com.pokemons.mvi.BaseViewIntents
import com.pokemons.mvi.rememberViewIntents

@Composable
fun CreateAccountScreen(model: CreateAccountViewModel) {
  MviScreen(
    viewModel = model,
    intents = rememberViewIntents(),
  ) { state, intent ->
    Column(
      modifier = Modifier
        .systemBarsPadding()
        .padding(bottom = 24.dp, start = 4.dp, end = 4.dp)
    ) {
      TopAppBar(
        modifier = Modifier.heightIn(52.dp),
        onClose = intent.navigateBack,
        title = state.title
      )
      VSpacer(size = 20.dp)
      TextField(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 12.dp),
        value = state.email,
        onValueChange = intent.changeEmail,
        placeholder = state.emailHint,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
      )
      VSpacer(size = 32.dp)
      TextField(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 12.dp),
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
      VSpacer(size = 16.dp)
      TextField(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 12.dp),
        value = state.repeatPassword,
        onValueChange = intent.changeRepeatPassword,
        placeholder = state.repeatPasswordHint,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = when(state.repeatPasswordSecurity) {
          PasswordSecurity.SHOW -> PasswordVisualTransformation()
          PasswordSecurity.HIDE -> VisualTransformation.None
        },
        trailingContent = {
          Text(
            modifier = Modifier.clickable(onClick = intent.showRepeatPassword),
            text = state.repeatPasswordSecurity.name,
            color = AppTheme.colors.grey,
            style = AppTheme.typography.caption1
          )
        }
      )
      WSpacer()
      PrimaryButton(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 12.dp),
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
  val changeRepeatPassword = intent<String>("changeRepeatPassword")
  val createAccount = intent("createAccount")
  val showPassword= intent("showPassword")
  val showRepeatPassword= intent("showPassword")
}

@Immutable
data class ViewState(
  val title: String = "Create Account",
  val email: String = "",
  val emailHint: String = "Email",
  val password: String = "",
  val passwordHint: String = "Password",
  val repeatPassword: String = "",
  val repeatPasswordHint: String = "Repeat the password",
  val createAccount: String = "Create Account",
  val passwordSecurity: PasswordSecurity = PasswordSecurity.SHOW,
  val repeatPasswordSecurity: PasswordSecurity = PasswordSecurity.SHOW,
)

@Composable
private fun TopAppBar(
  title: String,
  onClose: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Box(
    modifier = modifier.fillMaxWidth()
  ) {
    IconButton(
      modifier = Modifier.align(Alignment.CenterStart),
      onClick = onClose
    ) {
      Icon(
        painter = painterResource(id = R.drawable.ic_arrow_24),
        tint = AppTheme.colors.textPrimary,
        contentDescription = null,
      )
    }
    Text(
      modifier = Modifier.align(Alignment.Center),
      text = title,
      color = AppTheme.colors.textPrimary,
      style = AppTheme.typography.title
    )
  }
}
