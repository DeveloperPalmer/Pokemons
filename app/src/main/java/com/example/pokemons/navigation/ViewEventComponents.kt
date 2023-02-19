package com.example.pokemons.navigation

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.SnackbarData
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sla.core.ui.theme.AppTheme
import com.sla.mvi.event.DialogData
import com.sla.mvi.event.ViewEventComponents

internal fun getViewEventComponents(): ViewEventComponents {

  return object : ViewEventComponents {

    @Composable
    override fun Snackbar(data: SnackbarData) {
      Snackbar(snackbarData = data, elevation = 4.dp)
    }

    @Composable
    override fun ErrorSnackbar(data: SnackbarData) {

    }

    @Composable
    override fun Dialog(data: DialogData) {

    }
  }
}


@Composable
fun Snackbar(
  snackbarData: SnackbarData,
  modifier: Modifier = Modifier,
  actionOnNewLine: Boolean = false,
  shape: Shape = RoundedCornerShape(24.dp),
  backgroundColor: Color = AppTheme.colors.greyLight,
  contentColor: Color = AppTheme.colors.textPrimary,
  actionColor: Color = AppTheme.colors.textPrimary,
  elevation: Dp = 20.dp
) {
  androidx.compose.material.Snackbar(
    snackbarData,
    modifier,
    actionOnNewLine,
    shape,
    backgroundColor,
    contentColor,
    actionColor,
    elevation
  )
}
