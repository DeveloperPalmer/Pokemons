package com.sla.profile.ui.screen.profile

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sla.core.ui.R
import com.sla.core.ui.screen.MviScreen
import com.sla.core.ui.screen.OnBackPressedHandler
import com.sla.core.ui.theme.AppTheme
import com.sla.core.uikit.HSpacer
import com.sla.core.uikit.TopAppBar
import com.sla.core.uikit.VSpacer
import com.sla.core.uikit.WSpacer
import com.sla.mvi.BaseViewIntents
import com.sla.mvi.rememberViewIntents

@Composable
fun ProfileScreen(model: ProfileViewModel) {
  MviScreen(
    viewModel = model,
    intents = rememberViewIntents(),
  ) { state, intent ->
    OnBackPressedHandler(onBack = intent.navigateBack)
    Column(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding(),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      TopAppBar(
        onClick = intent.navigateBack,
        title = stringResource(id = R.string.profile)
      )
      VSpacer(size = 40.dp)
      Box(
        modifier = Modifier
          .size(88.dp)
          .background(color = AppTheme.colors.greyLight, shape = CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          modifier = Modifier.size(48.dp),
          painter = painterResource(id = R.drawable.ic_user_24),
          tint = AppTheme.colors.textPrimary,
          contentDescription = null
        )
      }
      VSpacer(size = 16.dp)
      Text(
        text = state.email,
        color = AppTheme.colors.textPrimary
      )
      VSpacer(size = 48.dp)
      ProfileItem(
        iconRes = R.drawable.ic_email_24,
        title = stringResource(id = R.string.change_email),
        leadingIcon = R.drawable.ic_arrow_right_24,
        onClick = intent.changeEmail
      )
      Divider(color = AppTheme.colors.greyLight)
      ProfileItem(
        iconRes = R.drawable.ic_setting_24,
        title = stringResource(id = R.string.change_password),
        leadingIcon = R.drawable.ic_arrow_right_24,
        onClick = intent.changePassword
      )
      Divider(color = AppTheme.colors.greyLight)
      ProfileItem(
        iconRes = R.drawable.ic_exit_24,
        title = stringResource(id = R.string.logout),
        isError = true,
        onClick = intent.logout
      )
      Divider(color = AppTheme.colors.greyLight)
    }
  }
}

class ViewIntents : BaseViewIntents() {
  val navigateBack = intent(name = "navigateBack")
  val changeEmail = intent("changeEmail")
  val changePassword = intent("changePassword")
  val logout = intent("logout")
}

@Immutable
data class ViewState(
  val email: String = "",
)

@Composable
private fun ProfileItem(
  @DrawableRes
  iconRes: Int,
  title: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  @DrawableRes
  leadingIcon: Int? = null,
  isError: Boolean = false
) {
  Row(
    modifier = modifier
      .clickable(onClick = onClick)
      .padding(16.dp)
  ) {
    Icon(
      painter = painterResource(id = iconRes),
      tint = if (isError) AppTheme.colors.error else AppTheme.colors.textPrimary,
      contentDescription = null
    )
    HSpacer(size = 16.dp)
    Text(
      text = title,
      color = if (isError) AppTheme.colors.error else AppTheme.colors.textPrimary,
      style = AppTheme.typography.body
    )
    WSpacer()
    if (leadingIcon != null) {
      Icon(
        painter = painterResource(id = leadingIcon),
        tint = if (isError) AppTheme.colors.error else AppTheme.colors.textPrimary,
        contentDescription = null
      )
    }
  }
}
