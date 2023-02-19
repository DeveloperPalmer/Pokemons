package com.sla.mvi.event

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.sla.mvi.resources.resolveRef
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

@Composable
fun ViewEventsHost(
  configurationsFlow: Flow<ViewEventPresentation>,
  viewEventComponents: ViewEventComponents,
  modifier: Modifier = Modifier,
) {
  val snackbarHostState = remember { SnackbarHostState() }
  val errorSnackbarHostState = remember { SnackbarHostState() }
  val dialogHostState = remember { DialogHostState() }
  val context = LocalContext.current

  LaunchedEffect(Unit) {
    configurationsFlow
      .onEach { configuration ->
        when (configuration) {
          is ViewEventPresentation.Snackbar -> {
            val message = context.resolveRef(configuration.message).toString()
            val actionLabel = configuration.actionLabel?.let { context.resolveRef(it).toString() }
            if (configuration.isError) {
              errorSnackbarHostState.showSnackbar(
                message,
                actionLabel,
                configuration.duration
              )
            } else {
              snackbarHostState.showSnackbar(
                message,
                actionLabel,
                configuration.duration
              )
            }
          }
          is ViewEventPresentation.Dialog -> {
            dialogHostState.showDialog(configuration)
          }
          ViewEventPresentation.None -> Unit
        }
      }
      .collect()
  }

  Box(
    modifier = modifier
      .navigationBarsPadding()
      .imePadding()
  ) {
    DialogHost(
      hostState = dialogHostState,
      dialog = { data ->
        viewEventComponents.Dialog(data = data)
      }
    )

    SnackbarHost(
      modifier = Modifier.align(Alignment.BottomCenter),
      hostState = snackbarHostState,
      snackbar = { data ->
        viewEventComponents.Snackbar(data)
      }
    )

    SnackbarHost(
      modifier = Modifier.align(Alignment.BottomCenter),
      hostState = errorSnackbarHostState,
      snackbar = { data ->
        viewEventComponents.ErrorSnackbar(data)
      }
    )
  }
}
