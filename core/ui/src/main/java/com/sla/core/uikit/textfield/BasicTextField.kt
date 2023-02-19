package com.sla.core.uikit.textfield

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sla.core.ui.theme.AppTheme
import com.sla.core.uikit.WSpacer
import kotlin.math.max

/**
 * Basic text field for internal usage
 *
 * @param value field value
 * @param onValueChange value change listener
 * @param modifier modifier to apply
 * @param enabled enabled state
 * @param readOnly read only state
 * @param label an optional label. Pass an empty string if you want to "reserve" a corresponding place in layout
 * @param placeholder an optional placeholder to display if field is not filled
 * @param helperText an optional helper text. Will use error color if [isError] is `true`.
 * Pass an empty string if you want to "reserve" a corresponding place in layout
 * @param counterMaxLength max chars counter length. This value must not be less than 1,
 * otherwise [IllegalArgumentException] will be thrown. If null than the counter will not be displayed
 * @param leadingContent a slot for leading content. It will be placed in the center of the box with the
 * **default min size** passed in the [defaultContentHeight] parameter
 * @param trailingContent a slot for trailing content. It will be placed in the center of the box with the
 * **default min size** passed in the [defaultContentHeight] parameter
 * @param isError error state
 * @param visualTransformation [VisualTransformation]
 * @param keyboardOptions [KeyboardOptions]
 * @param keyboardActions [KeyboardActions]
 * @param interactionSource [MutableInteractionSource]
 * @param minLines min lines a text field will take up.
 * Must not be less than 1 otherwise it will be set to 1
 * @param maxLines max available lines for wrapping. Has a higher priority than [minLines]
 * @param defaultContentHeight the minimum height of the text field. This value will be passed
 * to [leadingContent] and [trailingContent] as a parameter
 * @param textVerticalPadding the vertical padding that will be applied to the inner text
 * @param backgroundColor background color
 */
@Composable
internal fun BasicTextField(
  value: String,
  onValueChange: (String) -> Unit,
  modifier: Modifier = Modifier,
  testTag: String? = null,
  enabled: Boolean = true,
  readOnly: Boolean = false,
  label: String? = null,
  placeholder: String? = null,
  helperText: String? = null,
  counterMaxLength: Int? = null,
  leadingContent: @Composable ((defaultMinSize: Dp) -> Unit)? = null,
  trailingContent: @Composable ((defaultMinSize: Dp) -> Unit)? = null,
  isSuccess: Boolean = false,
  isError: Boolean = false,
  visualTransformation: VisualTransformation = VisualTransformation.None,
  keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
  keyboardActions: KeyboardActions = KeyboardActions.Default,
  interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
  minLines: Int = 1,
  maxLines: Int = Int.MAX_VALUE,
  defaultContentHeight: Dp = DefaultContentHeight,
  textVerticalPadding: Dp = DefaultVerticalPadding,
  backgroundColor: Color = AppTheme.colors.white,
) {
  // This idea is copied from the similar String-value overload of underlying material.TextField
  var textFieldValueState by remember { mutableStateOf(TextFieldValue(text = value)) }
  val textFieldValue = textFieldValueState.copy(text = value)
  var lastTextValue by remember(value) { mutableStateOf(value) }

  BasicTextField(
    value = textFieldValue,
    onValueChange = { newTextFieldValueState ->
      textFieldValueState = newTextFieldValueState

      val changed = lastTextValue != newTextFieldValueState.text
      lastTextValue = newTextFieldValueState.text

      if (changed) {
        onValueChange(newTextFieldValueState.text)
      }
    },
    modifier = modifier,
    testTag = testTag,
    enabled = enabled,
    readOnly = readOnly,
    label = label,
    placeholder = placeholder,
    helperText = helperText,
    counterMaxLength = counterMaxLength,
    leadingContent = leadingContent,
    trailingContent = trailingContent,
    isSuccess = isSuccess,
    isError = isError,
    visualTransformation = visualTransformation,
    keyboardOptions = keyboardOptions,
    keyboardActions = keyboardActions,
    interactionSource = interactionSource,
    minLines = minLines,
    maxLines = maxLines,
    defaultContentHeight = defaultContentHeight,
    textVerticalPadding = textVerticalPadding,
    backgroundColor = backgroundColor,
  )
}

/**
 * Basic text field for internal usage
 *
 * @param value field value
 * @param onValueChange value change listener
 * @param modifier modifier to apply
 * @param enabled enabled state
 * @param readOnly read only state
 * @param label an optional label. Pass an empty string if you want to "reserve" a corresponding place in layout
 * @param placeholder an optional placeholder to display if field is not filled
 * @param helperText an optional helper text. Will use error color if [isError] is `true`.
 * Pass an empty string if you want to "reserve" a corresponding place in layout
 * @param counterMaxLength max chars counter length. This value must not be less than 1,
 * otherwise [IllegalArgumentException] will be thrown. If null than the counter will not be displayed
 * @param leadingContent a slot for leading content. It will be placed in the center of the box with the
 * **default min size** passed in the [defaultContentHeight] parameter
 * @param trailingContent a slot for trailing content. It will be placed in the center of the box with the
 * **default min size** passed in the [defaultContentHeight] parameter
 * @param isError error state
 * @param visualTransformation [VisualTransformation]
 * @param keyboardOptions [KeyboardOptions]
 * @param keyboardActions [KeyboardActions]
 * @param interactionSource [MutableInteractionSource]
 * @param minLines min lines a text field will take up.
 * Must not be less than 1 otherwise it will be set to 1
 * @param maxLines max available lines for wrapping. Has a higher priority than [minLines]
 * @param defaultContentHeight the minimum height of the text field. This value will be passed
 * to [leadingContent] and [trailingContent] as a parameter
 * @param textVerticalPadding the vertical padding that will be applied to the inner text
 * @param backgroundColor background color
 */
@Suppress("ComplexMethod")
@Composable
internal fun BasicTextField(
  value: TextFieldValue,
  onValueChange: (TextFieldValue) -> Unit,
  modifier: Modifier = Modifier,
  testTag: String? = null,
  enabled: Boolean = true,
  readOnly: Boolean = false,
  label: String? = null,
  placeholder: String? = null,
  helperText: String? = null,
  counterMaxLength: Int? = null,
  leadingContent: @Composable ((defaultMinSize: Dp) -> Unit)? = null,
  trailingContent: @Composable ((defaultMinSize: Dp) -> Unit)? = null,
  isSuccess: Boolean = false,
  isError: Boolean = false,
  visualTransformation: VisualTransformation = VisualTransformation.None,
  keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
  keyboardActions: KeyboardActions = KeyboardActions.Default,
  interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
  minLines: Int = 1,
  maxLines: Int = Int.MAX_VALUE,
  defaultContentHeight: Dp = DefaultContentHeight,
  textVerticalPadding: Dp = DefaultVerticalPadding,
  backgroundColor: Color = AppTheme.colors.white,
) {
  Column(modifier = modifier) {
    val colors = textFieldColors(readOnly = readOnly, backgroundColor = backgroundColor)
    val typography = textFieldTypography()
    val textStyle = typography.bodyTextStyle.copy(
      color = colors.textColor(enabled = enabled).value
    )
    if (label != null) {
      Label(
        text = label,
        color = colors.labelColor(enabled = enabled).value,
        textStyle = typography.labelTextStyle,
        modifier = Modifier.padding(bottom = 4.dp)
      )
    }
    Box(
      modifier = Modifier
        .border(
          width = 1.dp,
          color = AppTheme.colors.greyLight,
          shape = RoundedCornerShape(12.dp)
        )
        .padding(vertical = 2.dp)
        .height(IntrinsicSize.Min)
        .fillMaxWidth()
    ) {
      val density = LocalDensity.current
      var fieldMinHeight by remember {
        mutableStateOf(
          if (maxLines == 1) {
            with(density) { defaultContentHeight.roundToPx() }
          } else null
        )
      }
      // An invisible text field that is filled with empty lines equal to the minLines value.
      // Updates the fieldMinHeightState with its own height value. This value is used to ensure
      // that the original text field is displayed correctly with the required minimum number of lines
      if (fieldMinHeight == null) {
        BasicTextField(
          value = value.text + "\n".repeat(max(0, minLines - 1)),
          onValueChange = {},
          modifier = Modifier
            .testTag(testTag.orEmpty())
            .alpha(0f)
            .focusable(false)
            .onSizeChanged { size -> fieldMinHeight = size.height },
          enabled = false,
          textStyle = textStyle,
          maxLines = maxLines
        )
      }
      if (fieldMinHeight != null) {
        BasicTextField(
          modifier = Modifier.testTag(testTag.orEmpty()),
          value = value,
          onValueChange = onValueChange,
          enabled = enabled,
          readOnly = readOnly,
          textStyle = textStyle,
          visualTransformation = visualTransformation,
          keyboardOptions = keyboardOptions,
          keyboardActions = keyboardActions,
          cursorBrush = SolidColor(colors.cursorColor(isError = isError).value),
          interactionSource = interactionSource,
          singleLine = maxLines == 1,
          maxLines = maxLines
        ) { innerTextField ->
          TextFieldDecoration(
            innerTextField = innerTextField,
            defaultContentHeight = defaultContentHeight,
            fieldMinHeight = with(LocalDensity.current) { fieldMinHeight!!.toDp() },
            textVerticalPadding = textVerticalPadding,
            colors = colors,
            placeholderTextStyle = typography.bodyTextStyle,
            interactionSource = interactionSource,
            modifier = Modifier
              .fillMaxWidth()
              .heightIn(min = defaultContentHeight),
            enabled = enabled,
            isSuccess = isSuccess,
            isError = isError,
            placeholder = if (placeholder != null && value.text.isEmpty()) {
              placeholder
            } else null,
            leadingContent = leadingContent,
            trailingContent = trailingContent,
            maxLines = maxLines
          )
        }
      }
    }
    if (helperText != null || counterMaxLength != null) {
      BottomContent(
        helperText = helperText,
        counterText = counterMaxLength?.run {
          require(counterMaxLength > 0) {
            "Counter max length is $counterMaxLength. This value cannot be less than 1"
          }
          "${value.text.length} / $counterMaxLength"
        },
        helperTextColor = colors.helperTextColor(enabled = enabled, success = isSuccess, error = isError).value,
        counterColor = colors.counterColor(enabled = enabled, error = isError).value,
        helperTextStyle = typography.helperTextStyle,
        counterTextStyle = typography.counterTextStyle,
        modifier = Modifier.padding(start = 4.dp, top = 2.dp, end = 4.dp)
      )
    }
  }
}

@Composable
private fun TextFieldDecoration(
  innerTextField: @Composable () -> Unit,
  defaultContentHeight: Dp,
  fieldMinHeight: Dp,
  textVerticalPadding: Dp,
  colors: DefaultTextFieldColors,
  placeholderTextStyle: TextStyle,
  interactionSource: MutableInteractionSource,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  isSuccess: Boolean = false,
  isError: Boolean = false,
  placeholder: String? = null,
  leadingContent: @Composable ((defaultMinSize: Dp) -> Unit)? = null,
  trailingContent: @Composable ((defaultMinSize: Dp) -> Unit)? = null,
  maxLines: Int = Int.MAX_VALUE,
) {
  Row(
    modifier = modifier
      .background(
        color = colors.backgroundColor().value,
        shape = TextFieldShape
      )
      .border(
        width = 1.dp,
        color = colors.borderColor(
          enabled = enabled,
          isSuccess = isSuccess,
          isError = isError,
          interactionSource = interactionSource
        ).value,
        shape = TextFieldShape
      ),
    verticalAlignment = Alignment.CenterVertically
  ) {
    if (leadingContent != null) {
      SideContent(
        defaultContentHeight = defaultContentHeight,
        content = leadingContent
      )
    }
    Box(
      modifier = Modifier
        .weight(1f)
        .heightIn(min = fieldMinHeight)
        .padding(
          top = textVerticalPadding,
          bottom = textVerticalPadding,
          start = if (leadingContent != null) 0.dp else DefaultHorizontalPadding,
          end = if (trailingContent != null) 0.dp else DefaultHorizontalPadding
        )
    ) {
      if (placeholder != null) {
        Text(
          text = placeholder,
          style = placeholderTextStyle,
          color = colors.placeholderColor(enabled = enabled).value,
          maxLines = maxLines,
          overflow = TextOverflow.Ellipsis
        )
      }
      innerTextField()
    }
    if (trailingContent != null) {
      SideContent(
        defaultContentHeight = defaultContentHeight,
        content = trailingContent
      )
    }
  }
}

@Composable
private fun Label(
  text: String,
  color: Color,
  textStyle: TextStyle,
  modifier: Modifier = Modifier
) {
  Text(
    text = text,
    modifier = modifier,
    style = textStyle,
    color = color
  )
}

@Composable
private fun SideContent(
  defaultContentHeight: Dp,
  content: @Composable (Dp) -> Unit
) {
  Box(
    modifier = Modifier
      .sizeIn(
        minWidth = defaultContentHeight,
        minHeight = defaultContentHeight
      )
      .fillMaxHeight(),
    contentAlignment = Alignment.Center
  ) {
    content(defaultContentHeight)
  }
}

@Composable
private fun BottomContent(
  helperText: String?,
  counterText: String?,
  helperTextColor: Color,
  counterColor: Color,
  helperTextStyle: TextStyle,
  counterTextStyle: TextStyle,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier,
  ) {
    if (helperText != null) {
      Text(
        modifier = Modifier.weight(1f),
        text = helperText,
        style = helperTextStyle,
        color = helperTextColor
      )
    } else {
      WSpacer()
    }
    if (counterText != null) {
      Text(
        modifier = Modifier.padding(start = 24.dp),
        text = counterText,
        style = counterTextStyle,
        color = counterColor
      )
    }
  }
}

@Suppress("TopLevelPropertyNaming") // Compose PascalCase naming
private const val AnimationDuration = 150
private val DefaultContentHeight = 58.dp
private val DefaultHorizontalPadding = 12.dp
private val DefaultVerticalPadding = 10.dp
private val TextFieldShape = RoundedCornerShape(12.dp)

@Composable
private fun textFieldTypography(): DefaultTextFieldTypography {
  return DefaultTextFieldTypography(
    bodyTextStyle = AppTheme.typography.body,
    labelTextStyle = AppTheme.typography.caption2,
    helperTextStyle = AppTheme.typography.caption2,
    counterTextStyle = AppTheme.typography.caption2
  )
}

@Composable
private fun textFieldColors(
  readOnly: Boolean,
  backgroundColor: Color,
): DefaultTextFieldColors {
  val colors = AppTheme.colors
  return DefaultTextFieldColors(
    textColor = if (readOnly) colors.textPrimary else colors.textPrimary,
    disabledTextColor = colors.white,
    cursorColor = colors.textPrimary,
    errorCursorColor = colors.error,
    focusedBorderColor = Color.Transparent,
    unfocusedBorderColor = Color.Transparent,
    successBorderColor = colors.white,
    errorBorderColor = colors.error,
    disabledBorderColor = Color.Transparent,
    backgroundColor = backgroundColor,
    labelColor = colors.textPrimary,
    disabledLabelColor = colors.white,
    helperTextColor = colors.error,
    disabledHelperTextColor = colors.white,
    successHelperTextColor = colors.white,
    errorHelperTextColor = colors.error,
    counterColor = colors.greyLight,
    disabledCounterColor = colors.white,
    errorCounterColor = colors.error,
    placeholderColor = colors.grey,
    disabledPlaceholderColor = colors.white
  )
}

@Immutable
private data class DefaultTextFieldTypography(
  val bodyTextStyle: TextStyle,
  val labelTextStyle: TextStyle,
  val helperTextStyle: TextStyle,
  val counterTextStyle: TextStyle,
)

@Suppress("LongParameterList")
@Immutable
private class DefaultTextFieldColors(
  private val textColor: Color,
  private val disabledTextColor: Color,
  private val cursorColor: Color,
  private val errorCursorColor: Color,
  private val focusedBorderColor: Color,
  private val unfocusedBorderColor: Color,
  private val successBorderColor: Color,
  private val errorBorderColor: Color,
  private val disabledBorderColor: Color,
  private val backgroundColor: Color,
  private val labelColor: Color,
  private val disabledLabelColor: Color,
  private val helperTextColor: Color,
  private val disabledHelperTextColor: Color,
  private val successHelperTextColor: Color,
  private val errorHelperTextColor: Color,
  private val counterColor: Color,
  private val disabledCounterColor: Color,
  private val errorCounterColor: Color,
  private val placeholderColor: Color,
  private val disabledPlaceholderColor: Color
) {

  @Composable
  fun borderColor(
    enabled: Boolean,
    isSuccess: Boolean,
    isError: Boolean,
    interactionSource: InteractionSource
  ): State<Color> {
    val focused by interactionSource.collectIsFocusedAsState()

    val targetValue = when {
      !enabled -> disabledBorderColor
      isSuccess -> successBorderColor
      isError -> errorBorderColor
      focused -> focusedBorderColor
      else -> unfocusedBorderColor
    }
    return if (enabled) {
      animateColorAsState(targetValue, tween(durationMillis = AnimationDuration))
    } else {
      rememberUpdatedState(targetValue)
    }
  }

  @Composable
  fun backgroundColor(): State<Color> {
    return rememberUpdatedState(backgroundColor)
  }

  @Composable
  fun placeholderColor(enabled: Boolean): State<Color> {
    return rememberUpdatedState(if (enabled) placeholderColor else disabledPlaceholderColor)
  }

  @Composable
  fun labelColor(enabled: Boolean): State<Color> {
    return rememberUpdatedState(if (enabled) labelColor else disabledLabelColor)
  }

  @Composable
  fun helperTextColor(
    enabled: Boolean,
    success: Boolean,
    error: Boolean
  ): State<Color> {
    val targetValue = when {
      !enabled -> disabledHelperTextColor
      success -> successHelperTextColor
      error -> errorHelperTextColor
      else -> helperTextColor
    }
    return rememberUpdatedState(targetValue)
  }

  @Composable
  fun counterColor(
    enabled: Boolean,
    error: Boolean
  ): State<Color> {
    val targetValue = when {
      !enabled -> disabledCounterColor
      error -> errorCounterColor
      else -> counterColor
    }
    return rememberUpdatedState(targetValue)
  }

  @Composable
  fun textColor(enabled: Boolean): State<Color> {
    return rememberUpdatedState(if (enabled) textColor else disabledTextColor)
  }

  @Composable
  fun cursorColor(isError: Boolean): State<Color> {
    return rememberUpdatedState(if (isError) errorCursorColor else cursorColor)
  }
}
