package com.pokemons.feature.core.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onStart

fun <T1, T2> Flow<T1>.mapDistinctChanges(transform: suspend (T1) -> T2): Flow<T2> {
  return this.map(transform).distinctUntilChanged()
}

fun <T1, T2 : Any> Flow<T1>.mapDistinctNotNullChanges(transform: suspend (T1) -> T2?): Flow<T2> {
  return this.mapNotNull(transform).distinctUntilChanged()
}

fun <T> Flow<T>.throttleFirst(periodMillis: Long): Flow<T> {
  require(periodMillis > 0) { "period should be positive" }
  return flow {
    var lastTime = 0L
    collect { value ->
      val currentTime = System.currentTimeMillis()
      if (currentTime - lastTime >= periodMillis) {
        lastTime = currentTime
        emit(value)
      }
    }
  }
}

fun <T : Any> Flow<T>.pairwise(): Flow<Pair<T, T>> = flow {
  var prev: T? = null
  collect { next ->
    prev?.let { emit(Pair(it, next)) }
    prev = next
  }
}

@Suppress("UseOnStartEmit") // this is the definition :)
fun <T> Flow<T>.onStartEmit(value: T): Flow<T> {
  return this.onStart { emit(value) }
}
