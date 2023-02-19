package com.sla.core.routing.di

import javax.inject.Qualifier
import kotlin.reflect.KClass

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class DestinationsIn(val scope: KClass<*>)
