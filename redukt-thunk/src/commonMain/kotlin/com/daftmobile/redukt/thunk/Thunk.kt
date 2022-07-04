package com.daftmobile.redukt.thunk

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.middleware.consumingMiddleware
import com.daftmobile.redukt.core.scope.DispatchScope

interface CoreThunk<State> : Action {
    suspend fun DispatchScope<State>.execute()
}

open class Thunk<State>(val block: suspend DispatchScope<State>.() -> Unit) : CoreThunk<State> {
    override suspend fun DispatchScope<State>.execute() = block()
}

fun <State> thunkMiddleware() = consumingMiddleware<State, Thunk<State>> { it.run { execute() } }