package com.sla.feature.core.data

import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow

interface AppPreferences {
  val data: Flow<Preferences>
  suspend fun <T> get(key: Preferences.Key<T>): T?
  suspend fun edit(transform: suspend (MutablePreferences) -> Unit)
}
