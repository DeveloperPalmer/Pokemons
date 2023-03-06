package com.pokemons.core.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

/**
 * You can call the OnLifecycleEvent() method to handle the lifecycle events
 * of any composable screen. Parameters are equal to lifecycle methods.
 */
@Composable
fun OnLifecycleEvent(
  onCreate: (() -> Unit)? = null,
  onStart: (() -> Unit)? = null,
  onResume: (() -> Unit)? = null,
  onPause: (() -> Unit)? = null,
  onStop: (() -> Unit)? = null,
  onDestroy: (() -> Unit)? = null,
  onAny: (() -> Unit)? = null
) {
  OnLifecycleEvent { _, event ->
    when (event) {
      Lifecycle.Event.ON_CREATE -> onCreate?.invoke()
      Lifecycle.Event.ON_START -> onStart?.invoke()
      Lifecycle.Event.ON_RESUME -> onResume?.invoke()
      Lifecycle.Event.ON_PAUSE -> onPause?.invoke()
      Lifecycle.Event.ON_STOP -> onStop?.invoke()
      Lifecycle.Event.ON_DESTROY -> onDestroy?.invoke()
      Lifecycle.Event.ON_ANY -> onAny?.invoke()
    }
  }
}

@Composable
private fun OnLifecycleEvent(
  onEvent: (owner: LifecycleOwner, event: Lifecycle.Event) -> Unit
) {
  val lifecycleOwner = LocalLifecycleOwner.current

  DisposableEffect(lifecycleOwner) {
    val lifecycle = lifecycleOwner.lifecycle
    val observer = LifecycleEventObserver { owner, event ->
      onEvent(owner, event)
    }
    lifecycle.addObserver(observer)
    onDispose { lifecycle.removeObserver(observer) }
  }
}
