package com.sla.feature.login.routing

import com.sla.core.routing.BaseFlowCoordinator1
import com.sla.core.routing.di.DestinationsIn
import com.sla.core.ui.scaffold.MainScaffoldController
import com.sla.core.ui.scaffold.popMainTo
import com.sla.core.ui.scaffold.pushMain
import com.sla.feature.core.domain.di.SingleIn
import com.sla.feature.login.domain.LoginModel
import com.sla.mvi.screen.Destination
import com.sla.navigation.ScreenRegistry
import com.sla.feature.login.domain.di.LoginScope
import com.sla.feature.login.ui.navigation.FlowEvent
import com.sla.feature.login.ui.navigation.LoginRoute
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject

object LoginFlow {

  enum class Result {
    Dismissed,
  }

  object Params

  @SingleIn(LoginScope::class)
  class Coordinator @Inject constructor(
    @DestinationsIn(LoginScope::class)
    destinations: Set<@JvmSuppressWildcards Destination>,
    flowEvent: MutableSharedFlow<FlowEvent>,
    screenRegistry: ScreenRegistry,
    loginModel: LoginModel,
    private val controller: MainScaffoldController,
  ) : BaseFlowCoordinator1<FlowEvent, Params, Result>(
    controller, destinations, flowEvent, screenRegistry
  ) {

    init {
      loginModel.start(coroutineScope)
    }

    override fun onFlowStart(params: Params) {
      controller.pushMain(LoginRoute.Login)
    }

    override fun handleEvent(event: FlowEvent) {
      when (event) {
        is FlowEvent.CreateAccountDismissed -> {
          controller.popMainTo(LoginRoute.Login)
        }
        is FlowEvent.CreateAccountRequested -> {
          controller.pushMain(LoginRoute.CreateAccount)
        }
        is FlowEvent.LoginDismissed -> {
          finish(Result.Dismissed)
        }
      }
    }
  }
}
