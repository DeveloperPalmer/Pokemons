package com.sla.feature.app.data.storage

import android.content.Context
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.sla.feature.app.domain.AppScope
import com.sla.feature.core.data.AppPreferences
import com.sla.feature.core.domain.di.ApplicationContext
import com.sla.feature.core.domain.di.SingleIn
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class AndroidAppPreferences @Inject constructor(
  @ApplicationContext private val context: Context,
) : AppPreferences {
  private val Context.preferences by preferencesDataStore(name = "MapsMePrefs")

  override val data: Flow<Preferences> = context.preferences.data
  override suspend fun <T> get(key: Preferences.Key<T>): T? {
    return context.preferences.data.firstOrNull()?.get(key)
  }

  override suspend fun edit(transform: suspend (MutablePreferences) -> Unit) {
    context.preferences.edit(transform)
  }
}
