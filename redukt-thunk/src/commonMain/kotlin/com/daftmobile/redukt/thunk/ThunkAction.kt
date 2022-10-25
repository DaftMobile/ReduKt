package com.daftmobile.redukt.thunk

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.DispatchScope
import com.daftmobile.redukt.core.JobAction

public sealed interface ThunkAction<out State> : Action

public interface ThunkApi<out State> : ThunkAction<State> {
    public fun DispatchScope<@UnsafeVariance State>.execute()
}

public interface CoThunkApi<out State> : ThunkAction<State>, JobAction {
    public suspend fun DispatchScope<@UnsafeVariance State>.execute()
}

public open class Thunk<State>(public val block: DispatchScope<State>.() -> Unit) : ThunkApi<State> {

    override fun DispatchScope<State>.execute(): Unit = block()
}

public open class CoThunk<State>(public val block: suspend DispatchScope<State>.() -> Unit) : CoThunkApi<State> {
    override suspend fun DispatchScope<State>.execute(): Unit = block()
}

