package com.pokemons.mvi

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

open class BaseViewIntents {
  private val intentRelay = MutableSharedFlow<UiIntent>(extraBufferCapacity = 5)

  internal val stream: Flow<UiIntent> = intentRelay

  fun intent(name: String = "name-${System.currentTimeMillis()}"): UiIntentFactory0 {
    return UiIntentFactory0(name, intentRelay)
  }

  fun <T : Any> intent(name: String = "name-${System.currentTimeMillis()}"): UiIntentFactory1<T> {
    return UiIntentFactory1(name, intentRelay)
  }
}
