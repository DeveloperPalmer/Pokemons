package com.sla.core.routing

import com.sla.core.ui.scaffold.MainScaffoldController
import com.sla.core.ui.scaffold.MainScaffoldState
import com.sla.core.ui.scaffold.SequenceId
import com.sla.core.ui.scaffold.StateSequenceException
import com.sla.mvi.screen.Destination
import com.sla.navigation.ScreenRegistry
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import com.sla.navigation.coordinator.FlowCoordinator
import com.sla.navigation.coordinator.FlowCoordinator1
import com.sla.navigation.coordinator.FlowCoordinatorHook
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean

abstract class BaseFlowCoordinator<Event, Result>(
  mainScaffoldController: MainScaffoldController,
  destinations: Set<Destination>,
  flowEvents: MutableSharedFlow<Event>,
  screenRegistry: ScreenRegistry,
) : BaseFlowCoordinator1<Event, Unit, Result>(mainScaffoldController, destinations, flowEvents, screenRegistry),
  FlowCoordinator<Event, Result> {

  final override fun start(onFlowFinish: ((Result) -> Unit), onError: (Throwable) -> Result) {
    start(Unit, onFlowFinish, onError)
  }

  final override fun onFlowStart(params: Unit): Unit = onFlowStart()
  open fun onFlowStart() = Unit
}

abstract class BaseFlowCoordinator1<Event, Params, Result>(
  private val mainScaffoldController: MainScaffoldController,
  private val destinations: Set<Destination>,
  private val flowEvents: MutableSharedFlow<Event>,
  private val screenRegistry: ScreenRegistry,
) : FlowCoordinator1<Event, Params, Result> {

  val flowId: SequenceId = SequenceId("${this::class.java}")

  private var onFinish: ((Result) -> Unit)? = null
  private val errorHandler = CoroutineExceptionHandler { _, e ->
    Timber.e(e, "coordinator error")
  }
  val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default + errorHandler)
  private val hooks: MutableList<FlowCoordinatorHook<Event, Params, Result>> = mutableListOf()
  private val isFinishing = AtomicBoolean(false)
  private var _params: Params? = null
  override val params: Params
    get() = _params ?: error("params is null. You need to call the start() method first")

  final override fun start(
    params: Params,
    onFlowFinish: ((Result) -> Unit),
    onError: (Throwable) -> Result
  ) {
    val handler = CoroutineExceptionHandler { _, error ->
      if (error !is CancellationException) {
        handleFlowError(error, onError)
      }
    }
    coroutineScope.launch(handler) {
      logFlowStart(params)
      screenRegistry.register(destinations)
      hooks.forEach { it.preStart(params) }
      onFinish = onFlowFinish
      mainScaffoldController.startStateSequence(flowId, initialState(params))
      _params = params
      onFlowStart(params)
      flowEvents
        .onEach { event ->
          hooks.forEach { hook -> hook.preHandleEvent(event) }
          handleEvent(event)
          hooks.forEach { hook -> hook.postHandleEvent(event) }
        }
        .launchIn(this)
      hooks.forEach { it.postStart(params) }
    }
  }

  final override fun finish(result: Result) {
    runBlocking {
      finishAsync(result = result).join()
    }
  }

  final override fun finishAsync(result: Result): Job {
    return coroutineScope.launch {
      if (!isFinishing.get()) {
        isFinishing.set(true)
        hooks.forEach { it.preFinish(result) }
        logFlowFinishing(result)
        mainScaffoldController.finishStateSequence(flowId)
        screenRegistry.unregister(destinations)
        onFinish?.invoke(result)
        hooks.forEach { it.postFinish(result) }
      }
    }
      .apply { invokeOnCompletion { coroutineScope.cancel() } }
  }

  private fun handleFlowError(error: Throwable, onErrorReturn: (Throwable) -> Result) {
    coroutineScope.launch {
      if (!isFinishing.get()) {
        isFinishing.set(true)
        val result = onErrorReturn(error)
        hooks.forEach { it.preFinish(result) }
        logFlowError(error, result)
        if (error !is StateSequenceException) {
          mainScaffoldController.finishStateSequence(flowId)
          screenRegistry.unregister(destinations)
        }
        onFinish?.invoke(result)
        hooks.forEach { it.postFinish(result) }
      }
    }.invokeOnCompletion { coroutineScope.cancel() }
  }

  abstract fun onFlowStart(params: Params)
  open fun initialState(params: Params): (MainScaffoldState) -> MainScaffoldState = { it }

  abstract override fun handleEvent(event: Event)

  override fun addHook(hook: FlowCoordinatorHook<Event, Params, Result>) {
    hooks.add(hook)
  }

  override fun removeHook(hook: FlowCoordinatorHook<Event, Params, Result>) {
    hooks.remove(hook)
  }

  protected fun runOnMainThread(body: () -> Unit) {
    coroutineScope.launch(Dispatchers.Main.immediate) { body() }
  }

  private fun logFlowStart(params: Params) {
    Timber.d("┌─────────────────────────────────────────────────────────")
    Timber.d("│ Starting [${flowDebugName()}] flow")
    Timber.d("│  params=$params")
    Timber.d("└─────────────────────────────────────────────────────────")
  }

  private fun logFlowFinishing(result: Result) {
    Timber.d("┌─────────────────────────────────────────────────────────")
    Timber.d("│ Finishing [${flowDebugName()}] flow")
    Timber.d("│  result=$result")
    Timber.d("└─────────────────────────────────────────────────────────")
  }

  private fun logFlowError(error: Throwable, result: Result) {
    Timber.d("┌─────────────────────────────────────────────────────────")
    Timber.d("│ Interrupting [${flowDebugName()}] flow")
    Timber.d("│  result=$result")
    Timber.d("│  error=$error")
    Timber.d("└─────────────────────────────────────────────────────────")
    Timber.e(error)
  }

  private fun flowDebugName() = this.javaClass.canonicalName!!.split('.').findLast { it.endsWith("Flow") }
}