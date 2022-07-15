package com.daftmobile.redukt.core

import com.daftmobile.redukt.core.context.element.DispatchCoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Executes [ActionDispatcher.dispatch] in a new coroutine using [DispatchCoroutineScope] from [ActionDispatcher.dispatchContext].
 * The outcome of dispatched [action] should not be expected after this function returns.
 * @param action to be dispatched
 */
public fun ActionDispatcher.launchDispatch(action: Action): Job = dispatchContext[DispatchCoroutineScope].launch { dispatch(action) }

public suspend fun ActionDispatcher.dispatchIfPresent(action: Action?) {
    action?.let { dispatch(it) }
}