package com.daftmobile.redukt.thunk

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.DispatchScope
import com.daftmobile.redukt.core.LocalDispatchScope
import com.daftmobile.redukt.core.SuspendAction
import com.daftmobile.redukt.core.coroutines.DispatchCoroutineScope
import com.daftmobile.redukt.core.coroutines.launchForeground
import com.daftmobile.redukt.core.middleware.Middleware
import com.daftmobile.redukt.core.middleware.consumingMiddleware
import com.daftmobile.redukt.core.withLocalScope

public sealed interface ThunkAction<State> : Action

public interface ThunkApi<State> : ThunkAction<State> {
    public fun LocalDispatchScope<State>.execute()
}

public interface CoThunkApi<State> : ThunkAction<State>, SuspendAction {
    public suspend fun LocalDispatchScope<State>.execute()
}

public open class Thunk<State>(public val block: DispatchScope<State>.() -> Unit) : ThunkApi<State> {

    override fun LocalDispatchScope<State>.execute(): Unit = block()
}

public open class CoThunk<State>(public val block: suspend DispatchScope<State>.() -> Unit) : CoThunkApi<State> {
    override suspend fun LocalDispatchScope<State>.execute(): Unit = block()
}

public fun <State> thunkMiddleware(): Middleware<State> = consumingMiddleware<_, ThunkAction<State>> { thunk ->
    when (thunk) {
        is CoThunkApi -> launchForeground {
            withLocalScope(DispatchCoroutineScope(this)) {
                thunk.run { execute() }
            }
        }
        is ThunkApi -> thunk.run { execute() }
    }
}
