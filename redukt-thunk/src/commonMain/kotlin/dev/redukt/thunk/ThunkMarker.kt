package dev.redukt.thunk

import dev.redukt.core.Action
import dev.redukt.core.DispatchScope
import dev.redukt.core.coroutines.ForegroundJobAction

/**
 * Marker interface for every thunk.
 *
 * @see ThunkAction
 */
public sealed interface ThunkMarker<State> : Action

/**
 * It is an equivalent of [Redux thunk](https://redux.js.org/usage/writing-logic-thunks#what-is-a-thunk).
 * In ReduKt it is an [Action] that contains some logic in [execute] method. It can interact with a store through [DispatchScope].
 *
 * **Important**: [thunkMiddleware] must be added to a store, because it is responsible for calling [execute]
 */
public interface ThunkAction<State> : ThunkMarker<State> {
    public fun DispatchScope<State>.execute()
}

/**
 * It is very similar to [ThunkAction], however [execute] suspends. It should be used to perform asynchronous logic.
 * **Important**: It is a [ForegroundJobAction] and [thunkMiddleware] is responsible for launching foreground coroutine.
 */
public interface CoThunkAction<State> : ThunkMarker<State>, ForegroundJobAction {
    public suspend fun DispatchScope<State>.execute()
}

/**
 * A [ThunkAction] implementation that executes a [block].
 */
public open class Thunk<State>(public val block: DispatchScope<State>.() -> Unit) : ThunkAction<State> {

    override fun DispatchScope<State>.execute(): Unit = block()
}

/**
 * A [CoThunkAction] implementation that executes a [block].
 */
public open class CoThunk<State>(public val block: suspend DispatchScope<State>.() -> Unit) : CoThunkAction<State> {
    override suspend fun DispatchScope<State>.execute(): Unit = block()
}

