package com.github.lupuuss.redukt.thunk

import com.github.lupuuss.redukt.core.context.element.coroutineScope
import com.github.lupuuss.redukt.core.middleware.consumingMiddleware
import kotlinx.coroutines.launch

fun <State> thunkMiddleware() = consumingMiddleware<State, Thunk> { thunk ->
    when (thunk) {
        is Thunk.Executable<*> -> thunk.cast<State>().run { execute() }
        is Thunk.Suspendable<*> -> coroutineScope.launch(thunk.coroutineContext, thunk.coroutineStart) {
            thunk.cast<State>().run { executeSuspendable() }
        }
    }
}

@Suppress("UNCHECKED_CAST")
internal fun <State> Thunk.Executable<*>.cast() = this as Thunk.Executable<State>

@Suppress("UNCHECKED_CAST")
internal fun <State> Thunk.Suspendable<*>.cast() = this as Thunk.Suspendable<State>