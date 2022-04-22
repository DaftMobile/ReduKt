package com.daftmobile.redukt.thunk

import com.daftmobile.redukt.core.context.element.coroutineScope
import com.daftmobile.redukt.core.middleware.consumingMiddleware
import kotlinx.coroutines.*

fun <State> thunkMiddleware() = consumingMiddleware<State, Thunk> { thunk ->
    when (thunk) {
        is Thunk.Executable<*> -> thunk.cast<State>().run { execute() }
        is Thunk.Launchable<*> -> coroutineScope.launch(thunk.context, thunk.start) {
            thunk.cast<State>().run { launch() }
        }
    }
}

@Suppress("UNCHECKED_CAST")
internal fun <State> Thunk.Executable<*>.cast() = this as Thunk.Executable<State>

@Suppress("UNCHECKED_CAST")
internal fun <State> Thunk.Launchable<*>.cast() = this as Thunk.Launchable<State>