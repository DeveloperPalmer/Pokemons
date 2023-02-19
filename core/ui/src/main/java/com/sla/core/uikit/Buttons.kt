package com.sla.core.uikit

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sla.core.ui.theme.AppTheme

@Composable
fun PrimaryButton(
  text: String,
  size: ButtonSize,
  appearance: ButtonAppearance,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  @DrawableRes iconResId: Int? = null,
  enabled: Boolean = true,
  loading: Boolean = false
) {
  Button(
    text = text,
    colors = ButtonDefaults.primaryButtonColors(appearance),
    size = size,
    onClick = onClick,
    modifier = modifier,
    iconResId = iconResId,
    enabled = enabled,
    loading = loading
  )
}

@Composable
fun SecondaryButton(
  text: String,
  size: ButtonSize,
  appearance: ButtonAppearance,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  @DrawableRes iconResId: Int? = null,
  enabled: Boolean = true,
  loading: Boolean = false
) {
  Button(
    text = text,
    colors = ButtonDefaults.secondaryButtonColors(appearance),
    size = size,
    onClick = onClick,
    modifier = modifier,
    iconResId = iconResId,
    enabled = enabled,
    loading = loading
  )
}

@Composable
fun TextButton(
  text: String,
  size: ButtonSize,
  appearance: ButtonAppearance,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  @DrawableRes iconResId: Int? = null,
  enabled: Boolean = true,
  loading: Boolean = false
) {
  Button(
    text = text,
    colors = ButtonDefaults.textButtonColors(appearance),
    size = size,
    onClick = onClick,
    modifier = modifier,
    iconResId = iconResId,
    enabled = enabled,
    loading = loading,
  )
}

@Composable
fun Button(
  backgroundColor: Color,
  contentColor: Color,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  borderColor: Color? = null,
  shape: RoundedCornerShape = ButtonDefaults.Shape,
  contentPadding: PaddingValues = PaddingValues(),
  content: @Composable RowScope.() -> Unit,
) {
  Row(
    modifier = modifier
      .surface(
        backgroundColor = backgroundColor,
        shape = shape,
        onClick = onClick,
        enabled = enabled
      )
      .then(
        if (borderColor != null) Modifier.border(width = 1.dp, shape = shape, color = borderColor)
        else Modifier
      )
      .padding(contentPadding),
    horizontalArrangement = Arrangement.Center,
    verticalAlignment = Alignment.CenterVertically
  ) {
    CompositionLocalProvider(
      LocalContentColor provides contentColor
    ) {
      content()
    }
  }
}

@Composable
private fun ButtonContent(
  text: String,
  size: ButtonSize,
  modifier: Modifier = Modifier,
  @DrawableRes iconResId: Int? = null,
  loading: Boolean = false
) {
  Box(
    modifier = modifier,
    contentAlignment = Alignment.Center
  ) {
    val contentAlpha by animateFloatAsState(targetValue = if (loading) 0f else 1f)
    Row(
      modifier = Modifier.alpha(contentAlpha),
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically
    ) {
      if (iconResId != null) {
        val iconSize = ButtonDefaults.iconSize(size)
        Icon(
          modifier = Modifier.size(iconSize),
          painter = painterResource(iconResId),
          contentDescription = "button icon",
          tint = LocalContentColor.current
        )
        HSpacer(size = ButtonDefaults.IconSpacing)
      }
      val textStyle = ButtonDefaults.textStyle(size)
      Text(
        text = text,
        style = textStyle,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    }
    androidx.compose.animation.AnimatedVisibility(
      visible = loading,
      enter = fadeIn(),
      exit = fadeOut()
    ) {
      val loaderStrokeWidth = ButtonDefaults.loaderStrokeWidth(size)
      val loaderSize = ButtonDefaults.loaderSize(size)
      CircularProgressIndicator(
        modifier = Modifier.size(loaderSize),
        strokeWidth = loaderStrokeWidth,
        color = LocalContentColor.current
      )
    }
  }
}

@Composable
private fun Button(
  text: String,
  colors: ButtonColors,
  size: ButtonSize,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  @DrawableRes iconResId: Int? = null,
  enabled: Boolean = true,
  shape: RoundedCornerShape = ButtonDefaults.Shape,
  loading: Boolean = false,
) {
  val contentPadding = ButtonDefaults.contentPadding(size)
  Button(
    modifier = modifier,
    borderColor = colors.borderColor(enabled),
    backgroundColor = colors.backgroundColor(enabled),
    contentColor = colors.contentColor(enabled),
    onClick = onClick,
    enabled = enabled,
    shape = shape,
    contentPadding = contentPadding,
  ) {
    ButtonContent(
      text = text,
      size = size,
      iconResId = iconResId,
      loading = loading
    )
  }
}

@Composable
private fun Button(
  text: String,
  size: ButtonSize,
  backgroundBrush: Brush,
  disabledBackgroundColor: Color,
  contentColor: Color,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  @DrawableRes iconResId: Int? = null,
  enabled: Boolean = true,
  shape: RoundedCornerShape = ButtonDefaults.Shape,
  loading: Boolean = false,
) {
  val surfaceModifier = if (enabled) {
    Modifier.surface(
      backgroundBrush = backgroundBrush,
      shape = shape,
      onClick = onClick,
    )
  } else {
    Modifier.surface(
      backgroundColor = disabledBackgroundColor,
      shape = shape,
      onClick = onClick,
      enabled = false
    )
  }
  val contentPadding = ButtonDefaults.contentPadding(size)
  Row(
    modifier = modifier
      .then(surfaceModifier)
      .padding(contentPadding),
    horizontalArrangement = Arrangement.Center,
    verticalAlignment = Alignment.CenterVertically
  ) {
    CompositionLocalProvider(
      LocalContentColor provides contentColor
    ) {
      ButtonContent(
        text = text,
        size = size,
        iconResId = iconResId,
        loading = loading
      )
    }
  }
}

object ButtonDefaults {

  val Shape = RoundedCornerShape(12.dp)

  val IconSpacing = 4.dp

  fun iconSize(buttonSize: ButtonSize): Dp {
    return when (buttonSize) {
      ButtonSize.Small -> 16.dp
      ButtonSize.Large -> 24.dp
    }
  }

  fun contentPadding(buttonSize: ButtonSize): PaddingValues {
    return when (buttonSize) {
      ButtonSize.Small -> PaddingValues(horizontal = 12.dp, vertical = 8.dp)
      ButtonSize.Large -> PaddingValues(12.dp)
    }
  }

  @Composable
  fun textStyle(buttonSize: ButtonSize): TextStyle {
    return when (buttonSize) {
      ButtonSize.Small -> AppTheme.typography.caption1
      ButtonSize.Large -> AppTheme.typography.button
    }
  }

  @Composable
  fun loaderStrokeWidth(buttonSize: ButtonSize): Dp {
    return when (buttonSize) {
      ButtonSize.Small -> 1.5.dp
      ButtonSize.Large -> 2.5.dp
    }
  }

  @Composable
  fun loaderSize(buttonSize: ButtonSize): Dp {
    return when (buttonSize) {
      ButtonSize.Small -> 16.dp
      ButtonSize.Large -> 24.dp
    }
  }

  @Composable
  fun primaryButtonColors(appearance: ButtonAppearance): ButtonColors {
    return when (appearance) {
      ButtonAppearance.Accent -> ButtonColors(
        borderColor = Color.Transparent,
        backgroundColor = DefaultBackgroundColor,
        contentColor = DefaultContentColor,
        disabledBorderColor = Color.Transparent,
        disabledBackgroundColor = DefaultDisabledBackgroundColor,
        disabledContentColor = DefaultDisabledContentColor,
      )

      ButtonAppearance.Negative -> ButtonColors(
        borderColor = Color.Transparent,
        backgroundColor = AppTheme.colors.error,
        contentColor = DefaultContentColor,
        disabledBorderColor = Color.Transparent,
        disabledBackgroundColor = DefaultDisabledBackgroundColor,
        disabledContentColor = DefaultDisabledContentColor,
      )

      ButtonAppearance.Const -> ButtonColors(
        borderColor = Color.Transparent,
        backgroundColor = AppTheme.colors.primary,
        contentColor = AppTheme.colors.textPrimary,
        disabledBorderColor = Color.Transparent,
        disabledBackgroundColor = DefaultDisabledBackgroundColor,
        disabledContentColor = DefaultDisabledContentColor,
      )
    }
  }

  @Composable
  fun secondaryButtonColors(appearance: ButtonAppearance): ButtonColors {
    return when (appearance) {
      ButtonAppearance.Accent -> ButtonColors(
        borderColor = AppTheme.colors.primary,
        backgroundColor = AppTheme.colors.white,
        contentColor = AppTheme.colors.primary,
        disabledBorderColor = AppTheme.colors.grey,
        disabledBackgroundColor = DefaultDisabledBackgroundColor,
        disabledContentColor = DefaultDisabledContentColor,
      )

      ButtonAppearance.Negative -> ButtonColors(
        borderColor = Color.Transparent,
        backgroundColor = AppTheme.colors.primary,
        contentColor = AppTheme.colors.textPrimary,
        disabledBorderColor = Color.Transparent,
        disabledBackgroundColor = DefaultDisabledBackgroundColor,
        disabledContentColor = DefaultDisabledContentColor,
      )

      ButtonAppearance.Const -> ButtonColors(
        borderColor = Color.Transparent,
        backgroundColor = AppTheme.colors.primary,
        contentColor = AppTheme.colors.textPrimary,
        disabledBorderColor = Color.Transparent,
        disabledBackgroundColor = DefaultDisabledBackgroundColor,
        disabledContentColor = DefaultDisabledContentColor,
      )
    }
  }

  @Composable
  fun textButtonColors(appearance: ButtonAppearance): ButtonColors {
    return when (appearance) {
      ButtonAppearance.Accent -> ButtonColors(
        borderColor = Color.Transparent,
        backgroundColor = AppTheme.colors.primary,
        contentColor = AppTheme.colors.textPrimary,
        disabledBorderColor = Color.Transparent,
        disabledBackgroundColor = DefaultDisabledBackgroundColor,
        disabledContentColor = DefaultDisabledContentColor,
      )

      ButtonAppearance.Negative -> ButtonColors(
        borderColor = Color.Transparent,
        backgroundColor = AppTheme.colors.primary,
        contentColor = AppTheme.colors.textPrimary,
        disabledBorderColor = Color.Transparent,
        disabledBackgroundColor = DefaultDisabledBackgroundColor,
        disabledContentColor = DefaultDisabledContentColor,
      )

      ButtonAppearance.Const -> ButtonColors(
        borderColor = Color.Transparent,
        backgroundColor = AppTheme.colors.primary,
        contentColor = AppTheme.colors.textPrimary,
        disabledBorderColor = Color.Transparent,
        disabledBackgroundColor = DefaultDisabledBackgroundColor,
        disabledContentColor = DefaultDisabledContentColor,
      )
    }
  }

  private val DefaultBackgroundColor @Composable get() = AppTheme.colors.primary
  private val DefaultContentColor @Composable get() = AppTheme.colors.white
  private val DefaultDisabledContentColor @Composable get() = AppTheme.colors.grey
  private val DefaultDisabledBackgroundColor @Composable get() = AppTheme.colors.greyLight
}

@Stable
data class ButtonColors(
  private val borderColor: Color,
  private val backgroundColor: Color,
  private val contentColor: Color,
  private val disabledBorderColor: Color,
  private val disabledBackgroundColor: Color,
  private val disabledContentColor: Color,
) {

  fun borderColor(enabled: Boolean): Color {
    return if (enabled) borderColor else disabledBorderColor
  }

  fun backgroundColor(enabled: Boolean): Color {
    return if (enabled) backgroundColor else disabledBackgroundColor
  }

  fun contentColor(enabled: Boolean): Color {
    return if (enabled) contentColor else disabledContentColor
  }
}

enum class ButtonAppearance {
  Accent, Negative, Const
}

enum class ButtonSize {
  Small, Large
}
