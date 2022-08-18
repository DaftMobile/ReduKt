package com.daftmobile.redukt.thunk

import com.daftmobile.redukt.core.DispatchScope
import com.daftmobile.redukt.core.SuspendAction
import com.daftmobile.redukt.core.coroutines.launchForeground
import com.daftmobile.redukt.core.middleware.Middleware
import com.daftmobile.redukt.core.middleware.consumingMiddleware

public interface CoreThunk<State> : SuspendAction {
    public suspend fun DispatchScope<State>.execute()
}

public open class Thunk<State>(public val block: suspend DispatchScope<State>.() -> Unit) : CoreThunk<State> {
    override suspend fun DispatchScope<State>.execute(): Unit = block()
}

public fun <State> thunkMiddleware(): Middleware<State> = consumingMiddleware<_, CoreThunk<State>> { thunk ->
    launchForeground {
        thunk.run { execute() }
    }
}