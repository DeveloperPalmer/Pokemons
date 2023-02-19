package com.example.pokemons.navigation.router

import com.example.pokemons.AppScreenRegistry
import com.sla.core.ui.scaffold.*
import com.sla.feature.core.domain.mapDistinctChanges
import com.sla.feature.core.domain.mapDistinctNotNullChanges
import com.sla.feature.core.domain.randomUuid
import com.sla.mvi.screen.Route
import com.sla.mvi.screen.transition.ScreenTransitionsProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.kode.mvi.screen.transition.ScreenTransition

internal class AppMainScaffoldController(
  coroutineScope: CoroutineScope,
  private val screenRegistry: AppScreenRegistry,
  private val screenTransitionsProvider: ScreenTransitionsProvider,
  private val stateHolder: MainScaffoldStateHolder = MainScaffoldStateHolder(),
) : MainScaffoldController, MainScaffoldStateProvider by stateHolder {

  private val backStack = MutableStateFlow(listOf(rootEntry))
  // See NOTE_BACKSTACK_HANDLER_THREAD_SAFETY
  private val screenRegistryMutex = Mutex()

  init {
    backStack.mapDistinctChanges { it.lastOrNull()?.state?.route }
      .filterNotNull()
      .onEach { route ->
        screenRegistryMutex.withLock {
          screenRegistry.createScreen(route)
        }
      }
      .launchIn(coroutineScope)

    backStack.mapDistinctChanges { it.lastOrNull()?.state?.bottomSheet?.activeRouteOrNull() }
      .filterNotNull()
      .onEach { route ->
        screenRegistryMutex.withLock {
          screenRegistry.createScreen(route)
        }
      }
      .launchIn(coroutineScope)

    stateChanges
      .onEach { state -> backStack.update { stack -> stack.markApplied(state) } }
      .launchIn(coroutineScope)

    backStack
      .mapDistinctChanges { backstack ->
        backstack.map { it.state.route }
          .plus(backstack.mapNotNull { it.state.bottomSheet.activeRouteOrNull() })
          .toSet()
      }
      .onEach { destinations ->
        screenRegistryMutex.withLock {
          screenRegistry.destroyScreens { it !in destinations }
        }
      }
      .launchIn(coroutineScope)
  }

  override fun sequenceIdStateChanges(): Flow<SequenceId> {
    return backStack.mapDistinctNotNullChanges { backstack -> backstack.lastOrNull()?.state?.sequenceId }
  }

  override fun startStateSequence(id: SequenceId, initial: (MainScaffoldState) -> MainScaffoldState) {
    val previousSequenceId = backStack.value.lastOrNull()?.state?.sequenceId
    if (previousSequenceId == id) throw StateSequenceException("Consecutive sequences: $previousSequenceId -> $id")
    backStack.update { stack -> stack.startNewSequence(id, initial(latestState)) }
  }

  override fun finishStateSequence(id: SequenceId) {
    backStack.update { stack -> stack.filter { it.state.sequenceId != id }.map { it.copy(isApplied = false) } }
  }

  override fun requestStateChange(transform: (MainScaffoldState) -> MainScaffoldState) {
    backStack.update { stack -> stack.addEntry(transform(latestState)) }
  }

  override fun stateChangeRequests(): Flow<MainScaffoldState> {
    return backStack.mapDistinctNotNullChanges { backstack -> backstack.lastOrNull()?.takeIf { !it.isApplied }?.state }
  }

  override fun registerRouteTransition(route: Route, transition: ScreenTransition) {
    screenTransitionsProvider.registerTransition(route, transition)
  }

  override fun findPreviousMainRoute(): Route? {
    val mainRoute = latestState.route
    return backStack.value.dropLastWhile { it.state.route == mainRoute }.lastOrNull()?.state?.route
  }

  override fun findMainRouteByPreviousStackSteps(previousSteps: Int): Route? {
    val mainRoute = latestState.route
    val lastBackStack = backStack.value.dropLastWhile { it.state.route == mainRoute }
    return lastBackStack.getOrNull(lastBackStack.size - previousSteps)?.state?.route
  }

  override val latestState: MainScaffoldState
    get() = backStack.value.lastOrNull()?.takeIf { !it.isApplied }?.state ?: readMainScaffoldState()

  override fun stateChanges(sequenceId: SequenceId): Flow<MainScaffoldState> {
    return stateHolder.stateChanges.filter { state -> state.sequenceId == sequenceId }
  }
}

data class BackstackEntry(
  val state: MainScaffoldState,
  val isApplied: Boolean = false,
) {
  @JvmInline
  value class Id(val value: Any)
}

private fun List<BackstackEntry>.addEntry(
  state: MainScaffoldState
): List<BackstackEntry> {
  return plus(BackstackEntry(state.copy(id = MainScaffoldState.StateId(randomUuid())),))
}

private fun List<BackstackEntry>.startNewSequence(
  id: SequenceId,
  state: MainScaffoldState
): List<BackstackEntry> {
  return plus(BackstackEntry(state = state.copy(id = MainScaffoldState.StateId(randomUuid()), sequenceId = id)))
}

private fun List<BackstackEntry>.markApplied(
  state: MainScaffoldState
): List<BackstackEntry> {
  return map { if (it.state.id == state.id) it.copy(isApplied = true) else it }
}

private fun MainScaffoldState.BottomSheet.activeRouteOrNull(): Route? {
  return if (this is MainScaffoldState.BottomSheet.Active) route else null
}

private val rootEntry = BackstackEntry(state = rootState, isApplied = true)
