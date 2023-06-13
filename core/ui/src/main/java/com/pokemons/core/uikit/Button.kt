package com.pokemons.core.uikit

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonColors
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pokemons.core.ui.theme.AppTheme

@Composable
fun PrimaryButton(
  onClick: () -> Unit,
  text: String,
  modifier: Modifier = Modifier,
  color: Color = AppTheme.colors.primary,
  enabled: Boolean = true,
  beforeTextIcon: @Composable (() -> Unit)? = null,
  trailingIcon: @Composable (() -> Unit)? = null,
  isProgressBarVisible: Boolean = false
) {
  Button(
    modifier = modifier,
    onClick = onClick,
    enabled = enabled,
    colors = ButtonDefaults.primaryButtonColors(backgroundColor = color),
    beforeTextIcon = beforeTextIcon,
    trailingIcon = trailingIcon,
    isProgressBarVisible = isProgressBarVisible
  ) {
    ButtonText(text = text)
  }
}

@Composable
fun SecondaryButton(
  onClick: () -> Unit,
  text: String,
  modifier: Modifier = Modifier,
  color: Color = AppTheme.colors.white,
  enabled: Boolean = true,
  beforeTextIcon: @Composable (() -> Unit)? = null,
  trailingIcon: @Composable (() -> Unit)? = null,
  isProgressBarVisible: Boolean = false
) {
  Button(
    modifier = modifier.border(
      width = 1.dp,
      color = AppTheme.colors.primary,
      shape = RoundedCornerShape(12.dp)
    ),
    onClick = onClick,
    enabled = enabled,
    colors = ButtonDefaults.secondaryButtonColors(backgroundColor = color),
    beforeTextIcon = beforeTextIcon,
    trailingIcon = trailingIcon,
    isProgressBarVisible = isProgressBarVisible
  ) {
    ButtonText(text = text)
  }
}

@Composable
private fun Button(
  onClick: () -> Unit,
  enabled: Boolean,
  modifier: Modifier = Modifier,
  colors: ButtonColors = ButtonDefaults.primaryButtonColors(),
  beforeTextIcon: @Composable (() -> Unit)? = null,
  trailingIcon: @Composable (() -> Unit)? = null,
  isProgressBarVisible: Boolean = false,
  content: @Composable RowScope.() -> Unit
) {
  val iconPadding = 12.dp
  androidx.compose.material.Button(
    modifier = modifier.heightIn(min = 52.dp),
    contentPadding = PaddingValues(horizontal = iconPadding),
    onClick = onClick,
    enabled = enabled,
    colors = colors,
    shape = RoundedCornerShape(12.dp),
    elevation = null
  ) {
    // this is required to keep text horizontally centered
    val hasTrailingContent = trailingIcon != null || isProgressBarVisible
    val contentColor by colors.contentColor(enabled = enabled)
    CompositionLocalProvider(LocalContentColor provides contentColor) {

      if (hasTrailingContent) {
        SlotContent(
          modifier = Modifier
            .padding(end = iconPadding)
            .alpha(0f),
          icon = trailingIcon,
          isProgressBarVisible = isProgressBarVisible
        )
      }

      WSpacer()
      if (beforeTextIcon != null) {
        SlotContent(
          modifier = Modifier.padding(end = 8.dp),
          icon = beforeTextIcon,
          isProgressBarVisible = false
        )
      }
      content()
      WSpacer()

      if (hasTrailingContent) {
        SlotContent(
          modifier = Modifier
            .padding(start = iconPadding),
          icon = trailingIcon,
          isProgressBarVisible = isProgressBarVisible
        )
      }
    }
  }
}

@Composable
private fun ButtonText(text: String, textStyle: TextStyle = AppTheme.typography.button) {
  Text(
    text = text,
    style = textStyle,
    maxLines = 1,
    overflow = TextOverflow.Ellipsis
  )
}


@Composable
private fun SlotContent(
  icon: @Composable (() -> Unit)?,
  isProgressBarVisible: Boolean,
  modifier: Modifier = Modifier,
) {
  if (icon == null && !isProgressBarVisible) {
    // Simply reserve space for the progress bar or the trailing icon (when it appears)
    Box(modifier = modifier.size(24.dp))
  } else {
    Box(
      modifier = modifier,
      contentAlignment = Alignment.Center
    ) {
      if (isProgressBarVisible) {
        CircularProgressIndicator(
          modifier = Modifier
            .padding(end = 4.dp)
            .size(24.dp)
            .padding(1.dp),
          color = LocalContentColor.current,
          strokeWidth = 3.dp
        )
      } else if (icon != null) {
        icon()
      }
    }
  }
}

object ButtonDefaults {
  private const val alphaDisabled = 0.3f

  @Composable
  fun primaryButtonColors(
    backgroundColor: Color = AppTheme.colors.primary,
  ): ButtonColors {
    return MwmButtonColors(
      backgroundColor = backgroundColor,
      contentColor = AppTheme.colors.neutral,
      disabledBackgroundColor = AppTheme.colors.textPrimary,
      disabledContentColor = AppTheme.colors.textPrimary,
    )
  }

  @Composable
  fun secondaryButtonColors(
    backgroundColor: Color = AppTheme.colors.white,
  ): ButtonColors {
    return MwmButtonColors(
      backgroundColor = backgroundColor,
      contentColor = AppTheme.colors.primary,
      disabledBackgroundColor = backgroundColor,
      disabledContentColor = AppTheme.colors.textPrimary,
    )
  }

  @Composable
  fun textButtonColors(
    backgroundColor: Color = Color.Transparent,
    contentColor: Color = AppTheme.colors.neutral,
    disabledBackgroundColor: Color = backgroundColor,
    disabledContentColor: Color = contentColor.copy(alpha = alphaDisabled)
  ): ButtonColors {
    return MwmButtonColors(
      backgroundColor = backgroundColor,
      contentColor = contentColor,
      disabledBackgroundColor = disabledBackgroundColor,
      disabledContentColor = disabledContentColor,
    )
  }
}

@Immutable
private data class MwmButtonColors(
  private val backgroundColor: Color,
  private val contentColor: Color,
  private val disabledBackgroundColor: Color,
  private val disabledContentColor: Color,
) : ButtonColors {

  @Composable
  override fun backgroundColor(enabled: Boolean): State<Color> {
    return rememberUpdatedState(if (enabled) backgroundColor else disabledBackgroundColor)
  }

  @Composable
  override fun contentColor(enabled: Boolean): State<Color> {
    return rememberUpdatedState(if (enabled) contentColor else disabledContentColor)
  }
}