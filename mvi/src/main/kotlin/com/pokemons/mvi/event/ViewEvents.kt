package com.pokemons.mvi.event

import androidx.compose.material.SnackbarData
import androidx.compose.material.SnackbarDuration
import androidx.compose.runtime.Composable
import com.pokemons.mvi.resources.TextRef
import com.pokemons.mvi.resources.emptyTextRef

interface ViewEventConfig<VE : Any> {
  fun mapToPresentation(event: VE): ViewEventPresentation
}

interface ViewEventComponents {

  /**
   * Will be called for events with [ViewEventPresentation.Dialog].
   *
   * Snackbar will be dismissed automatically according to a provided duration OR when [SnackbarData.dismiss] will
   * be called on passed [data] object
   *
   * Can be overridden in `viewEventConfig` object of your Screen to render custom UI for this type of event.
   */
  @Composable
  fun Snackbar(data: SnackbarData)

  /**
   * Will be called for events with [ViewEventPresentation.Dialog].
   *
   * Snackbar will be dismissed automatically according to a provided duration OR when [SnackbarData.dismiss] will
   * be called on passed [data] object
   *
   * Can be overridden in `viewEventConfig` object of your Screen to render custom UI for this type of event.
   */
  @Composable
  fun ErrorSnackbar(data: SnackbarData)

  /**
   * Will be called for events with [ViewEventPresentation.Dialog].
   *
   * Can be overridden in `viewEventConfig` object of your Screen to render custom UI for this type of event.
   *
   * **Do not forget** to call [DialogData.dismiss] on passed [data] object when dialog should be closed
   */
  @Composable
  fun Dialog(data: DialogData)
}

sealed class ViewEventPresentation {
  data class Snackbar(
    val message: TextRef,
    val actionLabel: TextRef? = null,
    val duration: SnackbarDuration = SnackbarDuration.Short,
    val isError: Boolean = false,
  ) : ViewEventPresentation()

  sealed class Dialog : ViewEventPresentation() {
    abstract val title: TextRef
    abstract val text: TextRef?

    /**
     * Confirm dialog with two buttons. Cancellable by clicking outside.
     */
    data class Confirm(
      override val title: TextRef,
      override val text: TextRef,
      val dismissButtonText: TextRef,
      val confirmButtonText: TextRef,
      val onConfirm: (() -> Unit)? = null,
      val onDismiss: (() -> Unit)? = null,
    ) : Dialog()

    /**
     * Confirm dialog with two buttons. Contains either a title or text. Cancellable by clicking outside.
     */
    data class ConfirmTitleOrText(
      override val title: TextRef = emptyTextRef(),
      override val text: TextRef = emptyTextRef(),
      val dismissButtonText: TextRef,
      val confirmButtonText: TextRef,
      val onConfirm: (() -> Unit)? = null,
      val onDismiss: (() -> Unit)? = null,
    ) : Dialog()

    /**
     * Confirm dialog with two buttons and one check box. Cancellable by clicking outside.
     */
    data class ConfirmWithCheck(
      override val title: TextRef,
      override val text: TextRef,
      val dismissButtonText: TextRef,
      val confirmButtonText: TextRef,
      val checkBoxText: TextRef,
      val onConfirm: ((isChecked: Boolean) -> Unit)? = null,
      val onDismiss: ((isChecked: Boolean) -> Unit)? = null,
    ) : Dialog()

    /**
     * Confirm dialog with two buttons and one text field. Cancellable by clicking outside.
     */
    data class ConfirmWithTextField(
      override val title: TextRef,
      override val text: TextRef,
      val value: TextRef? = null,
      val dismissButtonText: TextRef,
      val confirmButtonText: TextRef,
      val onConfirm: ((value: String) -> Unit)? = null,
      val onDismiss: ((value: String) -> Unit)? = null,
    ) : Dialog()

    /**
     * Information message with a single  button. Cancellable by clicking outside.
     */
    data class Info(
      override val title: TextRef,
      val buttonText: TextRef,
      override val text: TextRef? = null,
      val onButtonClick: (() -> Unit)? = null,
      val onDismiss: (() -> Unit)? = null
    ) : Dialog()

    /**
     * Information message with a single button. Not cancellable by clicking outside.
     */
    data class Action(
      override val title: TextRef,
      override val text: TextRef,
      val buttonText: TextRef,
      val onButtonClick: (() -> Unit)? = null,
    ) : Dialog()
  }

  object None : ViewEventPresentation()
}
