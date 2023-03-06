package com.example.pokemons
import android.app.Application
import com.example.pokemons.di.AppComponent
import com.example.pokemons.di.AppComponentHolder
import com.example.pokemons.di.DaggerAppComponent

class Application: Application(), AppComponentHolder {

  private lateinit var _appComponent: AppComponent
  override val appComponent: AppComponent by lazy { _appComponent }

  override fun onCreate() {
    super.onCreate()
    _appComponent = DaggerAppComponent.builder()
      .applicationContext(this)
      .build()
  }
}