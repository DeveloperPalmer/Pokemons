package com.sla.core.content

import com.sla.core.ui.R
import com.sla.mvi.resources.TextRef
import com.sla.mvi.resources.resRef
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import ru.dimsuz.unicorn.coroutines.MachineDsl
import ru.kode.remo.JobFlow
import ru.kode.remo.JobState
import ru.kode.remo.Task
import ru.kode.remo.errors
import ru.kode.remo.successResults

// DO NOT try to adapt this to any use case. If your current needs are not basic enough,
// then DO NOT use this common shortcut, instead copy paste it into your machine definition
// and extend it as you wish
@Suppress("LongParameterList") // configuration function with many options
fun <VS : Any, P> MachineDsl<VS, *>.configureContentAction(
  task: Task<P>,
  onError: (VS, UiError) -> Unit = { _, _ -> },
  errorStateWrite: (VS, UiError?) -> VS = { state, _ -> state },
  contentStateRead: (VS) -> ContentLoadState,
  loadingStateWrite: (VS, Boolean) -> VS,
  dismissErrorIntent: Flow<Unit> = emptyFlow(),
  onSuccess: (state: VS, newState: VS, payload: P) -> Unit,
  successStateWrite: (state: VS, payload: P) -> VS = { state, _ -> state },
  errorMapper: (Throwable) -> UiError = { it.toAppUiError(action = MessageActions.Close { }) },
) {
  configureContentAction(
    onError = onError,
    errorStateWrite = errorStateWrite,
    flow = task.jobFlow,
    contentStateRead = contentStateRead,
    loadingStateWrite = loadingStateWrite,
    dismissErrorIntent = dismissErrorIntent,
    onSuccess = onSuccess,
    successStateWrite = successStateWrite,
    errorMapper = errorMapper
  )
}

// DO NOT try to adapt this to any use case. If your current needs are not basic enough,
// then DO NOT use this common shortcut, instead copy paste it into your machine definition
// and extend it as you wish
@Suppress("LongParameterList") // configuration function with many options
fun <VS : Any, P> MachineDsl<VS, *>.configureContentAction(
  flow: JobFlow<P>,
  onError: (VS, UiError) -> Unit = { _, _ -> },
  errorStateWrite: (VS, UiError?) -> VS = { state, _ -> state },
  contentStateRead: (VS) -> ContentLoadState,
  loadingStateWrite: (VS, Boolean) -> VS,
  dismissErrorIntent: Flow<Unit> = emptyFlow(),
  onSuccess: (state: VS, newState: VS, payload: P) -> Unit,
  successStateWrite: (state: VS, payload: P) -> VS = { state, _ -> state },
  errorMapper: (Throwable) -> UiError = { it.toAppUiError(action = MessageActions.Close { }) },
) {
  configureContentAction(
    onError = onError,
    errorStateWrite = errorStateWrite,
    dismissErrorIntent = dismissErrorIntent,
    jobStateChanges = flow.state,
    jobErrors = flow.errors(),
    jobSuccessResults = flow.successResults(),
    contentStateRead = contentStateRead,
    loadingStateWrite = loadingStateWrite,
    onSuccess = onSuccess,
    successStateWrite = successStateWrite,
    errorMapper = errorMapper
  )
}

// DO NOT try to adapt this to any use case. If your current needs are not basic enough,
// then DO NOT use this common shortcut, instead copy paste it into your machine definition
// and extend it as you wish
@Suppress("LongParameterList") // configuration function with many options
fun <VS : Any, P> MachineDsl<VS, *>.configureContentAction(
  jobStateChanges: Flow<JobState>,
  jobErrors: Flow<Throwable>,
  jobSuccessResults: Flow<P>,
  onError: (VS, UiError) -> Unit,
  errorStateWrite: (VS, UiError?) -> VS,
  dismissErrorIntent: Flow<Unit>,
  contentStateRead: (VS) -> ContentLoadState,
  loadingStateWrite: (VS, Boolean) -> VS,
  onSuccess: (state: VS, newState: VS, payload: P) -> Unit,
  successStateWrite: (state: VS, payload: P) -> VS,
  errorMapper: (Throwable) -> UiError = { it.toAppUiError(action = MessageActions.Close { }) },
) {
  onEach(dismissErrorIntent) {
    transitionTo { state, _ ->
      errorStateWrite(state, null)
    }
  }

  onEach(jobStateChanges) {
    transitionTo { state, jobState ->
      if (contentStateRead(state) == ContentLoadState.Ready) {
        loadingStateWrite(state, jobState == JobState.Running)
      } else state
    }
  }

  onEach(jobErrors) {
    transitionTo { state, error ->
      if (contentStateRead(state) == ContentLoadState.Ready) {
        errorStateWrite(state, errorMapper(error))
      } else state
    }
    action { state, _, error ->
      if (contentStateRead(state) == ContentLoadState.Ready) {
        onError(state, errorMapper(error))
      }
    }
  }

  onEach(jobSuccessResults) {
    transitionTo { state, jobPayload ->
      if (contentStateRead(state) == ContentLoadState.Ready) {
        successStateWrite(state, jobPayload)
      } else state
    }
    action { state, newState, jobPayload ->
      if (contentStateRead(state) == ContentLoadState.Ready) {
        onSuccess(state, newState, jobPayload)
      }
    }
  }
}

data class UiError(
  val message: UiMessage,
  val cause: Throwable? = null
) {
  constructor(title: TextRef) : this(UiMessage(title))
}

data class UiMessage(
  val title: TextRef,
  val description: TextRef? = null,
  val action: Action? = null,
) {
  data class Action(
    val name: TextRef,
    val listener: (() -> Unit),
  )
}

/**
 * A state which represents a loading progress of a whole screen content.
 * Used to render progress/error of *initial* screen load state.
 * This is not a state to represent progress/error state of subsequent screen actions.
 *
 * Examples:
 *  - loading of user profile: uses ContentLoadState
 *  - reacting to user pressing Login button: should not use ContentLoadState,
 *    because this is an action when content is already displayed.
 *    Progress/error is rendered differently in this case (usually)
 */
sealed class ContentLoadState {
  object NotStarted : ContentLoadState()
  object Loading : ContentLoadState()
  object Ready : ContentLoadState()
  data class Error(val error: UiError, val isRefreshInProgress: Boolean) : ContentLoadState()
}

@Suppress("FunctionName", "FunctionNaming")
object MessageActions {
  fun Close(listener: () -> Unit): UiMessage.Action {
    return UiMessage.Action(
      name = resRef(id = R.string.close),
      listener = listener
    )
  }
  fun Refresh(listener: () -> Unit): UiMessage.Action {
    return UiMessage.Action(
      name = resRef(id = R.string.try_again),
      listener = listener
    )
  }
}
