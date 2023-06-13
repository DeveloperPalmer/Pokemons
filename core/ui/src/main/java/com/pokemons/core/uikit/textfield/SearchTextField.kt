package com.pokemons.core.uikit.textfield

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pokemons.core.ui.theme.AppTheme

/**
 * Search text field with leading and trailing content default implementation
 *
 * @param value field value
 * @param onValueChange value change listener
 * @param onSearchAction lambda will be called when action search button is clicked
 * @param modifier modifier to apply
 * @param placeholder an optional placeholder to display if field is not filled
 * @param leadingContent a slot for leading content. Has a default implementation with a search icon.
 * Override if you need your own content, or pass null if you don't need anything
 * @param trailingContent a slot for trailing content. Has a default implementation with a clear icon button.
 * Override if you need your own content, or pass null if you don't need anything
 */
@Composable
fun SearchTextField(
  value: String,
  onValueChange: (String) -> Unit,
  onSearchAction: KeyboardActionScope.() -> Unit,
  modifier: Modifier = Modifier,
  placeholder: String? = "Search",
  leadingContent: @Composable ((defaultMinSize: Dp) -> Unit)? = null,
  trailingContent: @Composable ((defaultMinHeight: Dp) -> Unit)? = null,
) {
  BasicTextField(
    modifier = modifier,
    value = value,
    onValueChange = onValueChange,
    placeholder = placeholder,
    leadingContent = leadingContent,
    trailingContent = trailingContent,
    keyboardOptions = KeyboardOptions(
      imeAction = ImeAction.Search
    ),
    keyboardActions = KeyboardActions(
      onSearch = onSearchAction
    ),
    maxLines = 1,
    defaultContentHeight = 40.dp,
    textVerticalPadding = 8.dp
  )
}

/**
 * Search text field with leading and trailing content default implementation
 *
 * @param value field value
 * @param onValueChange value change listener
 * @param onSearchAction lambda will be called when action search button is clicked
 * @param modifier modifier to apply
 * @param placeholder an optional placeholder to display if field is not filled
 * @param leadingContent a slot for leading content. Has a default implementation with a search icon.
 * Override if you need your own content, or pass null if you don't need anything
 * @param trailingContent a slot for trailing content. Has a default implementation with a clear icon button.
 * Override if you need your own content, or pass null if you don't need anything
 */
@Composable
fun SearchTextField(
  value: TextFieldValue,
  onValueChange: (TextFieldValue) -> Unit,
  onSearchAction: KeyboardActionScope.() -> Unit,
  modifier: Modifier = Modifier,
  placeholder: String? = "Search",
  leadingContent: @Composable ((defaultMinSize: Dp) -> Unit)? = null,
  trailingContent: @Composable ((defaultMinHeight: Dp) -> Unit)? = null,
) {
  BasicTextField(
    modifier = modifier,
    value = value,
    onValueChange = onValueChange,
    placeholder = placeholder,
    leadingContent = leadingContent,
    trailingContent = trailingContent,
    keyboardOptions = KeyboardOptions(
      imeAction = ImeAction.Search
    ),
    keyboardActions = KeyboardActions(
      onSearch = onSearchAction
    ),
    maxLines = 1,
    defaultContentHeight = 40.dp,
    textVerticalPadding = 8.dp
  )
}
