package com.example.pokemons

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import com.example.feature.pokemons.routing.PokemonsFlow
import com.example.feature.pokemons.routing.di.PokemonsFlowComponent
import com.example.pokemons.component.AppNavHost
import com.example.pokemons.component.ScreenHost
import com.example.pokemons.component.ScreenNavHost
import com.example.pokemons.di.AppComponent
import com.example.pokemons.di.AppComponentHolder
import com.example.pokemons.di.ForegroundComponent
import com.example.pokemons.navigation.MainScaffold
import com.example.pokemons.navigation.router.AppMainScaffoldController
import com.example.pokemons.navigation.router.transition.AppScreenTransitionsProvider
import com.pokemons.core.ui.LocalScreenConfiguration
import com.pokemons.core.ui.scaffold.MainScaffoldController
import com.pokemons.core.ui.screen.BackPressedHandlers
import com.pokemons.core.ui.screen.LocalBackPressHandlers
import com.pokemons.core.ui.screen.OnLifecycleEvent
import com.pokemons.core.ui.theme.AppTheme
import com.pokemons.core.ui.theme.ColorTheme
import com.pokemons.feature.bottombar.ui.LocalBottomBarController
import com.pokemons.feature.bottombar.ui.LocalBottomBarPadding
import com.pokemons.feature.bottombar.ui.MainBottomAppBar
import com.pokemons.feature.core.domain.entity.ScreenConfiguration
import com.pokemons.feature.core.domain.entity.ScreenOrientation
import com.pokemons.feature.core.domain.entity.ScreenRotation
import com.pokemons.mvi.screen.ComposableScreen
import com.pokemons.mvi.screen.Destination
import com.pokemons.mvi.screen.Route
import com.pokemons.mvi.screen.transition.LocalScreenTransitionsProvider
import com.pokemons.navigation.ScreenRegistry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import java.util.concurrent.ConcurrentHashMap

class MainActivity : ComponentActivity() {

  // This wouldn't be needed if we had access to the "foregroundComponent" as a private field in this class.
  // But at the moment moving "foregroundComponent" out from "setContent" is not possible because its construction
  // depends on some objects which can only be constructed in a Composable context...
  // (this may change with the new navigation library)
  private val coroutineScope = CoroutineScope(Dispatchers.Default)
  private var carConnectionJob: Job? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val appComponent = (applicationContext as AppComponentHolder).appComponent
    setContent {
      val backPressedHandlers = remember { BackPressedHandlers() }
      val saveableStateHolder = rememberSaveableStateHolder()
      val transitionsProvider = remember { AppScreenTransitionsProvider() }
      val screenRegistry = remember { AppScreenRegistry(backPressedHandlers, saveableStateHolder) }
      val mainController = remember { AppMainScaffoldController(coroutineScope, screenRegistry, transitionsProvider) }
      val foregroundComponent = rememberForegroundComponent(appComponent, screenRegistry, mainController)

      OnBackPressedEffect(backPressedHandlers)

      AppTheme(
        useDarkTheme = true,
        currentTheme = ColorTheme.Green
      ) {
        CompositionLocalProvider(
          LocalScreenConfiguration provides ScreenConfiguration(ScreenOrientation.Portrait, ScreenRotation.PortraitUp),
          LocalBackPressHandlers provides backPressedHandlers,
          LocalScreenTransitionsProvider provides transitionsProvider,
        ) {
          val controller = remember { foregroundComponent.bottomBarController() }
          val pokemonsFlowComponent = remember { foregroundComponent.pokemonsFlowComponent() }
          AppFlowCoordinatorFinishEffect(pokemonsFlowComponent)
          MainScaffold(
            controller = mainController,
            content = { route ->
              ScreenHost(
                rotation = ScreenRotation.PortraitUp,
                bottomBar = {
                  MainBottomAppBar(controller = controller)
                },
                content = { paddingValues ->
                  CompositionLocalProvider(
                    LocalBottomBarPadding provides paddingValues,
                    LocalBottomBarController provides controller
                  ) {
                    ScreenNavHost(
                      modifier = Modifier.fillMaxWidth(),
                      screenRegistry = screenRegistry,
                      saveableStateHolder = saveableStateHolder,
                      route = route,
                      onInitGraph = {
                        pokemonsFlowComponent.coordinator().start(
                          onFlowFinish = { finish() },
                          onError = { PokemonsFlow.Result.Dismissed }
                        )
                      }
                    )
                  }
                }
              )
            },
            sheetContent = { route ->
              AppNavHost(
                screenRegistry = screenRegistry,
                saveableStateHolder = saveableStateHolder,
                route = route,
              )
            }
          )
        }
      }
    }
  }
  @Composable
  private fun rememberForegroundComponent(
    appComponent: AppComponent,
    screenRegistry: ScreenRegistry,
    mainScaffoldController: MainScaffoldController,
  ): ForegroundComponent {
    return remember {
      (appComponent as ForegroundComponent.ParentComponent).foregroundComponentFactory()
        .create(activity = this, screenRegistry = screenRegistry, mainScaffoldController = mainScaffoldController)
    }
  }
}

internal class AppScreenRegistry(
  private val backPressedHandlers: BackPressedHandlers,
  private val saveableStateHolder: SaveableStateHolder,
) : ScreenRegistry {

  private val registeredDestinations = ConcurrentHashMap<Route, Destination>().apply {
    put(Destination.root.route, Destination.root)
  }
  private val activeScreens = ConcurrentHashMap<Route, ComposableScreen>()

  override fun destroyScreens(predicate: (Route) -> Boolean) {
    activeScreens.keys.filterTo(mutableSetOf()) { predicate(it) }
      .forEach { route ->
        activeScreens[route]?.viewModel?.let { viewModel ->
          viewModel.destroy()
          saveableStateHolder.removeState(viewModel.id)
        }
        // See NOTE_BACKSTACK_HANDLER_THREAD_SAFETY
        backPressedHandlers.removeAll { it.value.startsWith(route.path) }
        activeScreens.remove(route)
      }
  }

  override fun screen(route: Route): ComposableScreen? {
    return activeScreens[route]
  }

  override fun register(destinations: Collection<Destination>) {
    registeredDestinations.putAll(destinations.map { it.route to it })
  }

  override fun unregister(destinations: Collection<Destination>) {
    val unregisteredRoutes = destinations.mapTo(mutableSetOf()) { it.route }
    registeredDestinations.keys.removeAll(unregisteredRoutes)
  }

  override fun createScreen(route: Route) {
    if (!activeScreens.containsKey(route)) {
      val screen = registeredDestinations[route]?.screen?.get()
        ?: error("Router error: destination ${route.path} is not registered")
      activeScreens[route] = screen
    }
  }
}

@Composable
internal fun AppFlowCoordinatorFinishEffect(appFlowComponent: PokemonsFlowComponent) {
  OnLifecycleEvent(
    onDestroy = {
      appFlowComponent.coordinator().finish(PokemonsFlow.Result.Dismissed)
    }
  )
}

@Composable
internal fun Activity.OnBackPressedEffect(handlers: BackPressedHandlers) {
  BackHandler(
    enabled = true,
    onBack = {
      handlers.lastEnabledOrNull()?.action?.invoke() ?: finish()
    }
  )
}


