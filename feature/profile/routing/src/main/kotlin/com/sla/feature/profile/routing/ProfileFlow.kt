package com.sla.feature.profile.routing

import com.sla.core.routing.BaseFlowCoordinator1
import com.sla.core.routing.di.DestinationsIn
import com.sla.core.ui.scaffold.MainScaffoldController
import com.sla.core.ui.scaffold.popMainTo
import com.sla.core.ui.scaffold.pushMain
import com.sla.feature.core.domain.di.SingleIn
import com.sla.feature.profile.domain.ProfileModel
import com.sla.feature.profile.domain.di.ProfileScope
import com.sla.mvi.screen.Destination
import com.sla.navigation.ScreenRegistry
import com.sla.profile.ui.navigation.FlowEvent
import com.sla.profile.ui.navigation.ProfileRoutes
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject

object ProfileFlow {


  enum class Result {
    Dismissed
  }

  object Params

  @SingleIn(ProfileScope::class)
  class Coordinator @Inject constructor(
    @DestinationsIn(ProfileScope::class)
    destinations: Set<@JvmSuppressWildcards Destination>,
    flowEvents: MutableSharedFlow<FlowEvent>,
    screenRegistry: ScreenRegistry,
    profileModel: ProfileModel,
    private val controller: MainScaffoldController,
  ) : BaseFlowCoordinator1<FlowEvent, Params, Result>(
    controller, destinations, flowEvents, screenRegistry
  ) {

    init {
      profileModel.start(coroutineScope)
    }

    override fun onFlowStart(params: Params) {
      controller.pushMain(route = ProfileRoutes.Profile)
    }

    override fun handleEvent(event: FlowEvent) {
      when (event) {
        is FlowEvent.ProfileDismissed -> finish(Result.Dismissed)
        is FlowEvent.ProfileEditRequested -> controller.pushMain(ProfileRoutes.ProfileEdit)
        is FlowEvent.ProfileEditDismissed -> controller.popMainTo(ProfileRoutes.Profile)
      }
    }
  }
}
