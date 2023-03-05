package com.example.pokemons
import android.app.Application
import com.example.pokemons.di.DaggerAppComponent

class Application: Application() {

  override fun onCreate() {
    super.onCreate()
    DaggerAppComponent.builder()
      .applicationContext(this)
      .build()
  }
}