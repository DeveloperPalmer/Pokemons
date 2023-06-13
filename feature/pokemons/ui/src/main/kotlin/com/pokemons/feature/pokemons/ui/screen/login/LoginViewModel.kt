package com.pokemons.feature.pokemons.ui.screen.login

import com.pokemons.feature.pokemons.ui.navigation.FlowEvent
import com.pokemons.feature.pokemons.ui.screen.login.PasswordSecurity.HIDE
import com.pokemons.feature.pokemons.ui.screen.login.PasswordSecurity.SHOW
import com.pokemons.mvi.BaseViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import ru.dimsuz.unicorn.coroutines.MachineDsl
import javax.inject.Inject

class LoginViewModel @Inject constructor(
  private val flowEvents: MutableSharedFlow<FlowEvent>
): BaseViewModel<ViewState, ViewIntents, Unit, Unit>() {

  override fun MachineDsl<ViewState, Unit>.buildMachine() {
    initial = ViewState() to null

    onEach(intent(ViewIntents::navigateBack)) {
      action { _, _, _ -> }
    }

    onEach(typedIntent(ViewIntents::changeEmail)) {
      transitionTo { state, query ->
        state.copy(email = query)
      }
    }
    onEach(typedIntent(ViewIntents::changePassword)) {
      transitionTo { state, query ->
        state.copy(password = query)
      }
    }
    onEach(intent(ViewIntents::showPassword)) {
      transitionTo { state, _ ->
        state.copy(
          passwordSecurity = when (state.passwordSecurity) {
            SHOW -> HIDE
            HIDE -> SHOW
          }
        )
      }
    }
    onEach(intent(ViewIntents::signIn)) {

    }
    onEach(intent(ViewIntents::createAccount)) {
      action { _, _, _ ->
        flowEvents.tryEmit(FlowEvent.CreateAccountRequested)
      }
    }
  }
}
