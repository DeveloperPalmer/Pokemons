package com.example.pokemons

import android.app.Application
import com.example.pokemons.di.AppComponent
import com.example.pokemons.di.AppComponentHolder
import com.example.pokemons.di.DaggerAppComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import timber.log.Timber

class Application : Application(), AppComponentHolder {

  private lateinit var _appComponent: AppComponent
  override val appComponent: AppComponent by lazy { _appComponent }

  override fun onCreate() {
    super.onCreate()
    _appComponent = DaggerAppComponent.builder()
      .applicationContext(this)
      .httpClient(getHttpClient())
      .build()
  }
}

private fun getHttpClient(): HttpClient {
  return HttpClient(Android.create()) {
    install(ContentNegotiation) {
      json(
        Json { ignoreUnknownKeys = true }
      )
    }
    install(Logging) {
      logger = defaultLogger()
      level = LogLevel.INFO
    }
    install(HttpTimeout) {
      connectTimeoutMillis = HTTP_TIMEOUT_MILLIS
      requestTimeoutMillis = HTTP_TIMEOUT_MILLIS
      socketTimeoutMillis = HTTP_TIMEOUT_MILLIS
    }
  }
}

private fun defaultLogger(): Logger {
  return object : Logger {
    override fun log(message: String) {
      Timber.d(message)
    }
  }
}
internal const val HTTP_TIMEOUT_MILLIS = 60_000L
