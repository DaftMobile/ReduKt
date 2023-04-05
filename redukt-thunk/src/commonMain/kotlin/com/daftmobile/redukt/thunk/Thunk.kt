package com.daftmobile.redukt.thunk

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.DispatchScope
import com.daftmobile.redukt.core.coroutines.ForegroundJobAction

/**
 * Marker interface for every thunk.
 *
 * @see ThunkMarker
 */
public sealed interface ThunkMarker<State> : Action

/**
 * It is an equivalent of [Redux thunk](https://redux.js.org/usage/writing-logic-thunks#what-is-a-thunk).
 * In ReduKt it is an [Action] that contains some logic in [execute] method. It can interact with a store through [DispatchScope].
 *
 * **Important**: [thunkMiddleware] must be added to a store, because it is responsible for calling [execute]
 */
public interface Thunk<State> : ThunkMarker<State> {
    public fun DispatchScope<State>.execute()
}

/**
 * It is very similar to [Thunk], however [execute] suspends. It should be used to perform asynchronous logic.
 * **Important**: It is a [ForegroundJobAction] and [thunkMiddleware] is responsible for launching foreground coroutine.
 */
public interface CoThunk<State> : ThunkMarker<State>, ForegroundJobAction {
    public suspend fun DispatchScope<State>.execute()
}

/**
 * Returns a [Thunk] that executes a [block].
 */
public inline fun <State> Thunk(
    crossinline block: DispatchScope<State>.() -> Unit
): Thunk<State> = object : Thunk<State> {
    override fun DispatchScope<State>.execute() = block()

}

/**
 * Returns a [CoThunk] that executes a [block].
 */
public inline fun <State> CoThunk(
    crossinline block: suspend DispatchScope<State>.() -> Unit
) : CoThunk<State> = object : CoThunk<State> {
    override suspend fun DispatchScope<State>.execute(): Unit = block()
}

/**
 * A [Thunk] implementation that executes a [block].
 */
public abstract class BaseThunk<State>(
    private val block: DispatchScope<State>.() -> Unit
) : Thunk<State> {
    final override fun DispatchScope<State>.execute(): Unit = block()
}

/**
 * A [CoThunk] implementation that executes a [block].
 */
public abstract class BaseCoThunk<State>(
    private val block: suspend DispatchScope<State>.() -> Unit
) : CoThunk<State> {
    final override suspend fun DispatchScope<State>.execute(): Unit = block()
}
