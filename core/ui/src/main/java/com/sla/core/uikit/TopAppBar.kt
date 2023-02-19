package com.sla.core.uikit

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.sla.core.ui.R
import com.sla.core.ui.theme.AppTheme

@Composable
fun TopAppBar(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  @DrawableRes
  leadingIcon: Int = R.drawable.ic_arrow_24,
  trailingContent: @Composable (BoxScope.() -> Unit)? = null,
  title: String? = null,
) {
  Box(modifier = modifier.fillMaxWidth()) {
    IconButton(
      modifier = Modifier.align(Alignment.CenterStart),
      onClick = onClick
    ) {
      Icon(
        painter = painterResource(id = leadingIcon),
        tint = AppTheme.colors.textPrimary,
        contentDescription = null,
      )
    }
    if (title != null) {
      Text(
        modifier = Modifier.align(Alignment.Center),
        text = title,
        color = AppTheme.colors.textPrimary,
        style = AppTheme.typography.title
      )
    }
    if (trailingContent != null) {
      Box(modifier = Modifier.align(Alignment.CenterEnd)) {
        trailingContent()
      }
    }
  }
}
