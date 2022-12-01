package dev.redukt.thunk

import dev.redukt.core.Action
import dev.redukt.core.DispatchScope
import dev.redukt.core.coroutines.ForegroundJobAction

public sealed interface ThunkAction<out State> : Action

public interface ThunkApi<out State> : ThunkAction<State> {
    public fun DispatchScope<@UnsafeVariance State>.execute()
}

public interface CoThunkApi<out State> : ThunkAction<State>, ForegroundJobAction {
    public suspend fun DispatchScope<@UnsafeVariance State>.execute()
}

public open class Thunk<State>(public val block: DispatchScope<State>.() -> Unit) : ThunkApi<State> {

    override fun DispatchScope<State>.execute(): Unit = block()
}

public open class CoThunk<State>(public val block: suspend DispatchScope<State>.() -> Unit) : CoThunkApi<State> {
    override suspend fun DispatchScope<State>.execute(): Unit = block()
}

