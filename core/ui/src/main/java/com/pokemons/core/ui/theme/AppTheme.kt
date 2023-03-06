package com.pokemons.core.ui.theme

import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.Colors
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

object AppTheme {
  val colors: AppColors
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current

  val typography: AppTypography
    @Composable
    @ReadOnlyComposable
    get() = LocalAppTypography.current
}

@Composable
fun AppTheme(
  useDarkTheme: Boolean,
  currentTheme: ColorTheme,
  typography: AppTypography = AppTheme.typography,
  content: @Composable () -> Unit
) {

  val colorPalette = remember(useDarkTheme, currentTheme) {
    if (useDarkTheme) {
      when (currentTheme) {
        ColorTheme.Violet -> DarkVioletColorPalette
        ColorTheme.Green -> DarkGreenColorPalette
      }
    } else {
      when (currentTheme) {
        ColorTheme.Violet -> LightVioletColorPalette
        ColorTheme.Green -> LightGreenColorPalette
      }
    }
  }

  MaterialTheme(
    colors = debugColors(),
    typography = MaterialTypography,
  ) {
    val textSelectionColors = TextSelectionColors(
      handleColor = colorPalette.brandColorAccent,
      backgroundColor = colorPalette.brandBackground
    )
    CompositionLocalProvider(
      LocalAppColors provides colorPalette,
      LocalAppTypography provides typography,
      LocalContentColor provides colorPalette.textPrimary,
      LocalTextSelectionColors provides textSelectionColors,
      LocalColorTheme provides currentTheme,
      content = content,
    )
  }
}

private val LocalAppColors = staticCompositionLocalOf<AppColors> {
  error("No AppColors provided")
}

/**
 * A Material [Colors] implementation which sets all colors to [debugColor] to discourage usage of
 * [MaterialTheme.colors] in preference to [.colors].
 */
fun debugColors() = Colors(
  primary = DebugColor,
  primaryVariant = DebugColor,
  secondary = DebugColor,
  secondaryVariant = DebugColor,
  background = DebugColor,
  surface = DebugColor,
  error = DebugColor,
  onPrimary = DebugColor,
  onSecondary = DebugColor,
  onBackground = DebugColor,
  onSurface = DebugColor,
  onError = DebugColor,
  isLight = true
)

private val DebugColor = Color.Magenta

@Immutable
enum class ColorTheme {
  Green, Violet
}

val LocalColorTheme = staticCompositionLocalOf<ColorTheme> {
  error("No ColorTheme provided")
}

