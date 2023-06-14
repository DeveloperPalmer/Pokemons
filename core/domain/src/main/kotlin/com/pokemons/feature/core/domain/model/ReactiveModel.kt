package com.pokemons.feature.core.domain.model

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.kode.remo.ReactiveModel
import timber.log.Timber

open class ReactiveModel(
  errorMapper: ((Throwable) -> Throwable)? = null,
  dispatcher: CoroutineDispatcher = Dispatchers.Default,
  private val log: (() -> String) -> Unit = { Timber.e(it()) }
) : ReactiveModel(errorMapper = errorMapper, dispatcher = dispatcher) {
  override fun onPostStart() {
    uncaughtExceptions
      .onEach { log { "uncaught error in ${this::class.simpleName}" } }
      .launchIn(scope)
  }
}