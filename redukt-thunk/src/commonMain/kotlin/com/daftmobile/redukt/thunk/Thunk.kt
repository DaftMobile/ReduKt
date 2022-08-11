package com.daftmobile.redukt.thunk

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.middleware.consumingMiddleware
import com.daftmobile.redukt.core.DispatchScope
import com.daftmobile.redukt.core.middleware.Middleware

public interface CoreThunk<State> : Action {
    public suspend fun DispatchScope<State>.execute()
}

public open class Thunk<State>(public val block: suspend DispatchScope<State>.() -> Unit) : CoreThunk<State> {
    override suspend fun DispatchScope<State>.execute(): Unit = block()
}

public fun <State> thunkMiddleware(): Middleware<State> = consumingMiddleware<_, CoreThunk<State>> { it.run { execute() } }