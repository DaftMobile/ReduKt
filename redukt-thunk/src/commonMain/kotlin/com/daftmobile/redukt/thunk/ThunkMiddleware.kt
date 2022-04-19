package com.daftmobile.redukt.thunk

import com.daftmobile.redukt.core.context.element.coroutineScope
import com.daftmobile.redukt.core.middleware.consumingMiddleware
import kotlinx.coroutines.*
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext

fun <State> thunkMiddleware() = consumingMiddleware<State, Thunk> { thunk ->
    when (thunk) {
        is Thunk.Executable<*> -> thunk.cast<State>().run { execute() }
        is Thunk.Suspendable<*> -> coroutineScope.launchUndispatchedIfPossible(
            context = thunk.coroutineContext,
            alwaysDispatch = thunk.alwaysDispatch
        ) {
            thunk.cast<State>().run { executeSuspendable() }
        }
    }
}

@Suppress("UNCHECKED_CAST")
internal fun <State> Thunk.Executable<*>.cast() = this as Thunk.Executable<State>

@Suppress("UNCHECKED_CAST")
internal fun <State> Thunk.Suspendable<*>.cast() = this as Thunk.Suspendable<State>

private fun CoroutineScope.launchUndispatchedIfPossible(
    context: CoroutineContext,
    alwaysDispatch: Boolean,
    block: suspend CoroutineScope.() -> Unit
) {
    val newContext = newCoroutineContext(context)
    val coroutineStart = when {
        alwaysDispatch -> CoroutineStart.DEFAULT
        coroutineContext == newContext -> CoroutineStart.UNDISPATCHED
        coroutineContext[ContinuationInterceptor] == newContext[ContinuationInterceptor] -> CoroutineStart.UNDISPATCHED
        else -> CoroutineStart.DEFAULT
    }
    launch(context, start = coroutineStart, block = block)
}