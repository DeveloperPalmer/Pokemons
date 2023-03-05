package com.pokemons.navigation.coordinator.entity

data class LifecycleCommand<R>(
  val start: () -> Unit,
  val finish: (result: R) -> Unit,
)
