package com.pokemons.core.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.pokemons.mvi.screen.Route

val LocalNavigationRoute = compositionLocalOf<Route> {
  error("No LocalNavigationRoute provided")
}

val LocalBackPressHandlers = compositionLocalOf<BackPressedHandlers> {
  error("No LocalBackPressHandlers provided")
}

class BackPressedHandlers {
  private val handlers = linkedMapOf<OnBackPressCallbackId, OnBackPressHandler>()

  // See NOTE_BACKSTACK_HANDLER_THREAD_SAFETY
  @Synchronized
  fun put(onBackPressCallbackId: OnBackPressCallbackId, handler: OnBackPressHandler) {
    handlers[onBackPressCallbackId] = handler
  }

  // See NOTE_BACKSTACK_HANDLER_THREAD_SAFETY
  @Synchronized
  fun removeAll(predicate: (OnBackPressCallbackId) -> Boolean) {
    handlers.keys.removeAll(predicate)
  }

  // See NOTE_BACKSTACK_HANDLER_THREAD_SAFETY
  @Synchronized
  fun lastEnabledOrNull(): OnBackPressHandler? {
    return handlers.entries.lastOrNull { it.value.isEnabled }?.value
  }
}

data class OnBackPressHandler(
  val action: () -> Unit,
  val isEnabled: Boolean = true,
)

/**
 * see NOTE_BACKSTACK_HANDLE_ORDER
 *
 * @param id is used to identify the callback when the screen leaves the composition and returns later.
 * If not specified, the default handler takes the id from  [LocalLifecycleOwner].
 * This will be enough in more cases.
 * If you use 2 or more backpress handlers on 1 screen you must specify this.
 * Note that it can't be changed after the screen has been recomposed, so use a constant value.
 * @param enabled default Compose flag to enable/disable callback
 * @param onBack lambda to be called on back pressed
 */
@Composable
fun OnBackPressedHandler(
  onBack: () -> Unit,
  id: OnBackPressCallbackId? = null,
) {
  val handlers = LocalBackPressHandlers.current
  val callbackId = id ?: OnBackPressCallbackId(LocalNavigationRoute.current.path)
  val callback = OnBackPressHandler(action = onBack, isEnabled = true)
  DisposableEffect(Unit) {
    handlers.put(callbackId, callback)
    onDispose {
      handlers.put(callbackId, callback.copy(isEnabled = false))
    }
  }
}

@JvmInline
value class OnBackPressCallbackId(val value: String)

//  NOTE_BACKSTACK_HANDLE_ORDER
//  Compose handles the callback like this("screen 1" = "S1", "screen 2" = "S2"):
//  1. Open S1 -> add its callback to the stack
//  2. Open S2 -> add its callback to top of the stack and remove S1 callback after
//  3. Back to S1 -> add S1 callback to top of the stack and remove S2 callback after
//
//  Now we are using 2 routers for navigation: AppRouter which is used to control
//  fullscreen screens and AppBottomSheetRouter which is used to control bottom sheet screens.
//  In this case the default callback handling works differently:
//  1. Open AppRouter S1 -> Stack: AppRouter S1
//  2. Open AppBottomSheetRouter S1 ->
//    Stack: AppRouter S1, AppBottomSheetRouter S1 (added without deleting of AppRouter S1)
//  3. Open AppRouter S2 -> Stack: AppBottomSheetRouter S1, AppRouter S2
//    (since the router is the same as S2, it has been added to the top of the stack and AppRouter S1 has been removed)
//  4. Back to AppBottomSheetRouter S1 from S2 ->
//    Stack: AppBottomSheetRouter S1, AppRouter S1
//    (this is because when we close AppRouter S2 we actually go back to S1 of the same router,
//    then AppRouter S1 will be added to the top of the stack and S2 will be removed)
//
//  The AppRouter S1 callback will then be executed first when the user presses the native back button.
//
//  To prevent this behavior, callbacks are added to an extra stack
//  where they won't be removed if the screen is still on the backstack.
//  The behavior can be described like this:
//  1. Open AppRouter S1 -> Extra Stack: AppRouter S1 -> Stack: AppRouter S1
//  2. Open AppBottomSheetRouter S1  ->
//    Extra Stack: AppRouter S1, AppBottomSheetRouter S1->
//    Stack: AppRouter S1, AppBottomSheetRouter S1
//  3. Open AppRouter S2 ->
//    Extra Stack: AppRouter S1(disabled), AppBottomSheetRouter S1, AppRouter S2 ->
//    Stack: AppBottomSheetRouter S1, AppRouter S2
//  4. Back to AppBottomSheetRouter S1 from S2 ->
//    Extra Stack: AppRouter S1(enabled), AppBottomSheetRouter S1, AppRouter S2
//    (We are looking for an Extra stack that has a callback with the id we are trying to add.
//    If we found it, then we take all enabled callbacks after the disabled one,
//    then remove them from the Compose stack. After that, we add the disabled callback
//    to the top of the Compose stack. Then we add the enabled callbacks that was removed earlier
//    to the top of the Compose stack. And finally, we mark the disabled callback as enabled) ->
//    Extra Stack: AppRouter S1, AppBottomSheetRouter S1, AppRouter S2(disabled) ->
//    Extra Stack: AppRouter S1, AppBottomSheetRouter S1
//    (the tail of disabled callbacks is removed each time a callback is disabled) ->
//    Stack: AppRouter S1, AppBottomSheetRouter S1
//
//  The id parameter is used to identify the callback when the screen leaves the composition and returns later.
//  If not specified, the default handler takes the id from  [LocalLifecycleOwner] which is [NavBackStackEntry].
//  This will be enough in more cases.
//  If you use 2 or more backpress handlers on 1 screen you must specify this.
//  Note that it can't be changed after the screen has been recomposed, so use a constant value.

// NOTE_BACKSTACK_HANDLER_THREAD_SAFETY
// Access to backstack handlers must be thread safe, because in case where one flow (A) ends and another flow (B)
// starts, flow A calls "removeAll" while flow B calls "put" and due to the fact that our routing functions can be
// called from different threads, these calls might overlap, and if they'll modify backstack handler list concurrently
// this will lead to the ConcurrentModificationException (at best).
