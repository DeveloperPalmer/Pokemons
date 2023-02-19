@file:OptIn(ExperimentalMaterialApi::class)

package com.sla.core.ui.scaffold

import androidx.compose.material.ExperimentalMaterialApi
import com.sla.core.ui.bottomsheet.BottomSheetState
import com.sla.feature.core.domain.mapDistinctChanges
import com.sla.mvi.screen.Destination
import com.sla.mvi.screen.Route
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import ru.kode.mvi.screen.transition.ScreenTransition
import java.lang.RuntimeException

interface MainScaffoldController {
  /**
   * Represents boundaries of a logical lifecycle of any given screen sequence, e.g. Flow
   */
  fun startStateSequence(id: SequenceId, initial: (MainScaffoldState) -> MainScaffoldState)
  fun finishStateSequence(id: SequenceId)

  fun requestStateChange(transform: (MainScaffoldState) -> MainScaffoldState)
  fun stateChangeRequests(): Flow<MainScaffoldState>

  fun sequenceIdStateChanges(): Flow<SequenceId>

  val latestState: MainScaffoldState
  val stateChanges: Flow<MainScaffoldState>
  fun stateChanges(sequenceId: SequenceId): Flow<MainScaffoldState>

  fun registerRouteTransition(route: Route, transition: ScreenTransition)

  fun findPreviousMainRoute(): Route?

  fun findMainRouteByPreviousStackSteps(previousSteps: Int): Route?
}

// Consecutive sequences of states (consecutive flow starts) is an error
class StateSequenceException(message: String) : RuntimeException(message)

@OptIn(ExperimentalMaterialApi::class)
fun MainScaffoldController.activeBottomSheetStateChanges(): Flow<BottomSheetState> {
  return stateChanges.map { it.bottomSheet }
    .filterIsInstance<MainScaffoldState.BottomSheet.Active>()
    .mapDistinctChanges { it.state }
}

fun MainScaffoldController.bottomSheetVisibilityChangeRequests(): Flow<Boolean> {
  return stateChangeRequests().map { it.bottomSheet }
    .filterIsInstance<MainScaffoldState.BottomSheet.Active>()
    .mapDistinctChanges { it.visibility == MainScaffoldState.BottomSheet.Visibility.Visible }
}

fun MainScaffoldController.bottomSheetVisibilityChanges(): Flow<Boolean> {
  return stateChanges.map { it.bottomSheet }
    .filterIsInstance<MainScaffoldState.BottomSheet.Active>()
    .mapDistinctChanges { it.visibility == MainScaffoldState.BottomSheet.Visibility.Visible }
}

fun MainScaffoldController.activeBottomSheetStateOrNull(): BottomSheetState? {
  return (latestState.bottomSheet as? MainScaffoldState.BottomSheet.Active)?.state
}

fun MainScaffoldController.activeBottomSheetRouteOrNull(): Route? {
  return (latestState.bottomSheet as? MainScaffoldState.BottomSheet.Active)?.route
}

@JvmInline
value class SequenceId(val value: String)

val rootState = MainScaffoldState(
  route = Destination.root.route,
  id = MainScaffoldState.StateId("root"),
  sequenceId = SequenceId("root"),
)
