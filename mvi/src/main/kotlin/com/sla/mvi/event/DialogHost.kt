package com.sla.mvi.event

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber

// NOTE: all this is modeled very closely to SnackbarHost, SnackbarDataImpl etc (except there's no auto-dismiss)
//       Check out those classes if you'll need to add support for queueing (using mutex) etc

@Composable
fun DialogHost(
  hostState: DialogHostState,
  dialog: @Composable (DialogData) -> Unit,
) {
  val currentDialogData = hostState.currentDialogData
  LaunchedEffect(currentDialogData) {
    if (currentDialogData != null) {
      // waiting for dismiss to be called on `currentDialogData`
      awaitCancellation()
    }
  }
  if (currentDialogData != null) {
    dialog(currentDialogData)
  }
}

interface DialogData {
  val configuration: ViewEventPresentation.Dialog

  fun dismiss()
}

@Stable
class DialogHostState {
  var currentDialogData by mutableStateOf<DialogData?>(null)
    private set

  suspend fun showDialog(configuration: ViewEventPresentation.Dialog) {
    if (currentDialogData != null) {
      Timber.e("Already showing a dialog for event: $currentDialogData. New event $configuration will be ignored.")
    } else {
      try {
        return suspendCancellableCoroutine { continuation ->
          currentDialogData = DialogDataImpl(
            configuration = configuration,
            continuation = continuation
          )
        }
      } finally {
        currentDialogData = null
      }
    }
  }

  @Stable
  private class DialogDataImpl(
    override val configuration: ViewEventPresentation.Dialog,
    private val continuation: CancellableContinuation<Unit>,
  ) : DialogData {

    override fun dismiss() {
      if (continuation.isActive) continuation.resume(Unit, onCancellation = null)
    }
  }
}
