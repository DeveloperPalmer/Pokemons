package com.sla.core.uikit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Tab
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sla.core.ui.theme.AppTheme

@Composable
fun TimePickerTab(
  selected: Boolean,
  onClick: () -> Unit,
  title: String,
  modifier: Modifier = Modifier,
) {
  Tab(
    modifier = modifier
      .heightIn(48.dp)
      .background(color = Color.Transparent)
      .padding(bottom = 2.dp),
    selected = selected,
    selectedContentColor = AppTheme.colors.primary,
    unselectedContentColor = AppTheme.colors.greyLight,
    onClick = onClick,
  ) {
    Text(
      text = title,
      color = if (selected) AppTheme.colors.textPrimary else AppTheme.colors.grey,
      style = AppTheme.typography.body,
    )
  }
}
