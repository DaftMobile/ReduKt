package com.daftmobile.redukt.core

import com.daftmobile.redukt.core.context.element.DispatchCoroutineScope
import kotlinx.coroutines.launch

/**
 * Executes [ActionDispatcher.dispatch] in a new coroutine using [DispatchCoroutineScope] from [ActionDispatcher.dispatchContext].
 * The outcome of dispatched [action] should not be expected after this function returns.
 * @param action to be dispatched
 */
fun ActionDispatcher.launchDispatch(action: Action) = dispatchContext[DispatchCoroutineScope].launch { dispatch(action) }

/**
 * Just like [ActionDispatcher.launchDispatch], executes [ActionDispatcher.dispatch] in a new coroutine using
 * [DispatchCoroutineScope] from [ActionDispatcher.dispatchContext]. Additionally, this extension joins created coroutine.
 * Therefore, the outcome of dispatched [action] could be expected after this function returns.
 * @param action to be dispatched
 */
suspend fun ActionDispatcher.syncDispatch(action: Action) = dispatchContext[DispatchCoroutineScope]
    .launch { dispatch(action) }
    .join()

suspend fun ActionDispatcher.dispatchIfPresent(action: Action?) = action?.let { dispatch(it) }