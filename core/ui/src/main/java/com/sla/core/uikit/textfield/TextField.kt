package com.sla.core.uikit.textfield

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import com.sla.core.ui.theme.AppTheme

/**
 * Filled text field
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
 * **default min size** parameter
 * @param trailingContent a slot for trailing content. It will be placed in the center of the box with the
 * **default min size** parameter
 * @param isSuccess success state
 * @param isError error state
 * @param visualTransformation [VisualTransformation]
 * @param keyboardOptions [KeyboardOptions]
 * @param keyboardActions [KeyboardActions]
 * @param interactionSource [MutableInteractionSource]
 * @param minLines min lines a text field will take up.
 * Must not be less than 1 otherwise it will be set to 1
 * @param maxLines max available lines for wrapping. Has a higher priority than [minLines]
 * @param backgroundColor background color
 */
@Composable
fun TextField(
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
  leadingContent: @Composable ((defaultMinHeight: Dp) -> Unit)? = null,
  trailingContent: @Composable ((defaultMinHeight: Dp) -> Unit)? = null,
  isSuccess: Boolean = false,
  isError: Boolean = false,
  visualTransformation: VisualTransformation = VisualTransformation.None,
  keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
  keyboardActions: KeyboardActions = KeyboardActions.Default,
  interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
  minLines: Int = 1,
  maxLines: Int = Int.MAX_VALUE,
  backgroundColor: Color = AppTheme.colors.white,
) {
  BasicTextField(
    modifier = modifier,
    testTag = testTag,
    value = value,
    onValueChange = onValueChange,
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
    backgroundColor = backgroundColor,
  )
}

/**
 * Filled text field
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
 * **default min size** parameter
 * @param trailingContent a slot for trailing content. It will be placed in the center of the box with the
 * **default min size** parameter
 * @param isSuccess success state
 * @param isError error state
 * @param visualTransformation [VisualTransformation]
 * @param keyboardOptions [KeyboardOptions]
 * @param keyboardActions [KeyboardActions]
 * @param interactionSource [MutableInteractionSource]
 * @param minLines min lines a text field will take up.
 * Must not be less than 1 otherwise it will be set to 1
 * @param maxLines max available lines for wrapping. Has a higher priority than [minLines]
 * @param backgroundColor background color
 */
@Composable
fun TextField(
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
  leadingContent: @Composable ((defaultMinHeight: Dp) -> Unit)? = null,
  trailingContent: @Composable ((defaultMinHeight: Dp) -> Unit)? = null,
  isSuccess: Boolean = false,
  isError: Boolean = false,
  visualTransformation: VisualTransformation = VisualTransformation.None,
  keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
  keyboardActions: KeyboardActions = KeyboardActions.Default,
  interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
  minLines: Int = 1,
  maxLines: Int = Int.MAX_VALUE,
  backgroundColor: Color = AppTheme.colors.white,
) {
  BasicTextField(
    modifier = modifier,
    testTag = testTag,
    value = value,
    onValueChange = onValueChange,
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
    backgroundColor = backgroundColor,
  )
}
