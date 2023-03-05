package com.pokemons.mvi

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.dimsuz.unicorn.coroutines.Machine
import ru.dimsuz.unicorn.coroutines.MachineDsl
import ru.dimsuz.unicorn.coroutines.machine
import timber.log.Timber
import java.util.UUID

abstract class BaseViewModel<VS : Any, VI : BaseViewIntents, VE : Any, A : Any>(
  val dispatcher: CoroutineDispatcher = Dispatchers.Default
) {
  val id = UUID.randomUUID().toString()
  protected val viewModelScope: CoroutineScope = CoroutineScope(
    dispatcher + CoroutineName("ViewModel") + SupervisorJob()
  )
  private val intentsScope = CoroutineScope(viewModelScope.coroutineContext + SupervisorJob())

  open val keepStateOnDetach: Boolean = false

  protected abstract fun MachineDsl<VS, A>.buildMachine()

  // lazy initialization is needed here because otherwise buildMachine() will be called during class initialization,
  // and before inheriting classes had a chance to initialize
  // their eventPayloads (things that are passed to onEach(...))
  private val machine: Machine<VS, A> by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
    machine(viewModelScope.coroutineContext) {
      buildMachine()
    }
  }
  private val stateFlow by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { MutableStateFlow(machine.initial.first) }
  val viewStateFlow: StateFlow<VS> get() = stateFlow

  private val eventsFlow = MutableSharedFlow<VE>()
  val viewEventsFlow: Flow<VE> = eventsFlow

  private val intentBinders = arrayListOf<IntentBinder<VI>>()

  private var isFirstViewAttach = true

  private val postViewAttachFlow = MutableSharedFlow<Boolean>()

  private fun bindTransitions() {
    machine.states
      .onEach { stateFlow.emit(it) }
      .catch { throw IllegalStateException("exception while reducing view state", it) }
      .launchIn(viewModelScope)
  }

  protected fun sendViewEvent(event: VE) {
    intentsScope.launch { eventsFlow.emit(event) }
  }

  protected fun postViewAttachEvents(skipFirstAttach: Boolean): Flow<Unit> {
    return postViewAttachFlow
      .filter { isFirstViewAttach -> !(isFirstViewAttach && skipFirstAttach) }
      .map { }
  }

  // NOTE_SCREEN_RESET
  open fun onAttach(intents: VI) {
    check(intents === intents) {
      "Expected View.intents to always return the same instance, internal error"
    }

    if (isFirstViewAttach || !keepStateOnDetach) {
      isFirstViewAttach = false
      bindTransitions()
    }
    intentBinders.map { binder ->
      val intent = binder.intent(intents)
      intents.stream
        .filter {
          intent.isOwnerOf(it)
        }
        .catch {
          throw IllegalStateException("intent \"${intent.name}\" has thrown an exception", it)
        }
        .onEach {
          binder.relay.emit(it.payload)
        }
        .launchIn(intentsScope)
    }

    intents.stream
      .onEach { intent ->
        val screenName =
          this@BaseViewModel::class.simpleName?.takeLastWhile { it != '.' }?.takeWhile { it != '$' }
        Timber.d("[$screenName, intent.name=${intent.name}]")
      }
      .launchIn(intentsScope)

    intentsScope.launch { postViewAttachFlow.emit(isFirstViewAttach) }

    if (isFirstViewAttach) {
      isFirstViewAttach = false
    }
  }

  // See NOTE_SCREEN_RESET and NOTE_ANDROID_AUTO_RESET
  open fun onDetach() {
    intentsScope.coroutineContext.cancelChildren()
    if (!keepStateOnDetach) {
      viewModelScope.coroutineContext.cancelChildren()
      stateFlow.update { machine.initial.first }
    }
  }

  fun destroy() {
    viewModelScope.cancel()
    intentsScope.cancel()
    postDestroy()
  }

  protected open fun postDestroy() = Unit

  @Suppress("UNCHECKED_CAST") // internally type of payload is irrelevant
  private fun <I : Any> intentInternal(bindOp: (VI) -> UiIntentFactory): Flow<I> {
    val binder = IntentBinder(
      bindOp,
      MutableSharedFlow()
    )
    intentBinders.add(binder)
    return binder.relay as Flow<I>
  }

  @Suppress("UNCHECKED_CAST") // we actually know the type of payload
  fun <I : Any> typedIntent(bindOp: (VI) -> UiIntentFactory1<I>): Flow<I> {
    return intentInternal(bindOp)
  }

  @Suppress("UNCHECKED_CAST") // we actually know the type of payload
  fun intent(bindOp: (VI) -> UiIntentFactory0): Flow<Unit> {
    return intentInternal(bindOp)
  }

  private data class IntentBinder<VI : Any>(
    val intent: (VI) -> UiIntentFactory,
    val relay: MutableSharedFlow<Any>,
  )
}

// NOTE_SCREEN_RESET
// Current MainScaffoldState-based screen navigation model purposefully does not support active flow screen destruction
// Instead VM reset its VS as soon as screen leaves composition and invokes machine initial side effect on recomposing

// NOTE_ANDROID_AUTO_RESET
// In Android Auto keepStateOnDetach is necessary in order to avoid building a template with the old state
// when returning to the previous screen - this can cause an increase in the quota of templates by one,
// which in turn can cause a fatal error
