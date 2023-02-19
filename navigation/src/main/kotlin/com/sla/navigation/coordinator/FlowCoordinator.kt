package com.sla.navigation.coordinator

import kotlinx.coroutines.Job

interface FlowCoordinator<Event, Result> : FlowCoordinator1<Event, Unit, Result> {
  /**
   * Запускает новый сценарий. Функция [onFlowFinish] будет вызвана по его окончанию с результатом выполнения сценария,
   * переданным в качестве аргумента.
   *
   * Если же понадобятся какие-то более изощрённые операции с бэкстеком, то для них уже лучше
   * использовать [FlowCoordinator1] в который передать какие-то специфичные `params` на вход.
   */
  fun start(onFlowFinish: (Result) -> Unit, onError: (Throwable) -> Result)
}

interface FlowCoordinator1<Event, Params, Result> {

  val params: Params

  /**
   * Запускает новый сценарий, принимающий на вход некие параметры [params].
   * Функция [onFlowFinish] будет вызвана по его окончанию с результатом выполнения сценария,
   * переданным в качестве аргумента.
   *
   * Если же понадобятся какие-то более изощрённые операции с бэкстеком, то для них уже лучше передать какие-то
   * специфичные `params` на вход.
   */
  fun start(params: Params, onFlowFinish: (Result) -> Unit, onError: (Throwable) -> Result)

  fun finish(result: Result)
  fun finishAsync(result: Result): Job

  fun handleEvent(event: Event)

  fun addHook(hook: FlowCoordinatorHook<Event, Params, Result>)
  fun removeHook(hook: FlowCoordinatorHook<Event, Params, Result>)
}

interface FlowCoordinatorHook<Event, Params, Result> {
  suspend fun preStart(params: Params) = Unit
  suspend fun postStart(params: Params) = Unit

  fun preHandleEvent(event: Event) = Unit
  fun postHandleEvent(event: Event) = Unit

  suspend fun preFinish(result: Result) = Unit
  suspend fun postFinish(result: Result) = Unit
}
