package com.pokemons.core.ui.scaffold

import com.pokemons.mvi.screen.Route
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import ru.kode.mvi.screen.transition.ScreenTransition

class DummyMainScaffoldController : MainScaffoldController {
  override fun startStateSequence(id: SequenceId, initial: (MainScaffoldState) -> MainScaffoldState) {
  }

  override fun finishStateSequence(id: SequenceId) {
  }

  override fun requestStateChange(transform: (MainScaffoldState) -> MainScaffoldState) {
  }

  override fun stateChangeRequests(): Flow<MainScaffoldState> {
    return emptyFlow()
  }

  override fun sequenceIdStateChanges(): Flow<SequenceId> {
    return emptyFlow()
  }

  override val latestState: MainScaffoldState = MainScaffoldState(
    route = DummyRoute,
    id = MainScaffoldState.StateId("dummy"),
    sequenceId = SequenceId("dummy"),
  )
  override val stateChanges: Flow<MainScaffoldState> = emptyFlow()

  override fun stateChanges(sequenceId: SequenceId): Flow<MainScaffoldState> {
    return emptyFlow()
  }

  override fun registerRouteTransition(route: Route, transition: ScreenTransition) {
  }

  override fun findPreviousMainRoute(): Route? {
    return null
  }

  override fun findMainRouteByPreviousStackSteps(previousSteps: Int): Route? {
    return null
  }
}

private object DummyRoute : Route {
  override val path: String = "/dummy/route"
}
