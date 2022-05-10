package com.daftmobile.redukt.thunk

import com.daftmobile.redukt.core.context.element.coroutineScope
import com.daftmobile.redukt.core.middleware.consumingMiddleware
import com.daftmobile.redukt.core.scope.DispatchScope
import kotlinx.coroutines.*

fun <State> thunkMiddleware() = consumingMiddleware<State, Thunk<*>> { thunk ->
    when (thunk) {
        is ExecuteThunk<*, *> -> thunk.cast<State>().executeWithContinuation(this)
        is SuspendThunk<*, *> -> thunk.cast<State>().executeWithContinuation(this@consumingMiddleware)
    }
}

private fun <State> SuspendThunk<State, Any?>.executeWithContinuation(dispatchScope: DispatchScope<State>) {
    val continuation = consumeContinuation()
    dispatchScope.coroutineScope.launch(context, start) {
        val result = runCatching { executeWith(dispatchScope) }
        continuation?.resumeWith(result) ?: result.onFailure { throw it }
    }
}

private fun <State> ExecuteThunk<State, Any?>.executeWithContinuation(dispatchScope: DispatchScope<State>) {
    val continuation = consumeContinuation()
    val result = runCatching { executeWith(dispatchScope) }
    continuation?.resumeWith(result) ?: result.onFailure { throw it }
}

private fun Thunk<Any?>.consumeContinuation() = continuation.also { continuation = null }