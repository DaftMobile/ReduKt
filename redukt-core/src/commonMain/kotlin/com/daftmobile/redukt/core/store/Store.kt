package com.daftmobile.redukt.core.store

import com.daftmobile.redukt.core.*
import com.daftmobile.redukt.core.context.DispatchContext
import com.daftmobile.redukt.core.middleware.MergedMiddlewareScope
import com.daftmobile.redukt.core.middleware.Middleware
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

public interface Store<State> : ActionDispatcher {

    public val state: StateFlow<State>
}

public inline val <State> Store<State>.currentState: State get() = state.value

internal class StoreImpl<State>(
    initialState: State,
    private val reducer: Reducer<State>,
    middlewares: List<Middleware<State>>,
    override val dispatchContext: DispatchContext
) : Store<State> {
    override val state = MutableStateFlow(initialState)

    private val coreScope = CoreDispatchScope(dispatchContext, this::dispatch, this.state::value)

    private val updateState: DispatchFunction = { action -> state.value = reducer(state.value, action) }
    private val dispatchPipeline: DispatchFunction = middlewares
        .reversed()
        .fold(updateState) { next, current ->
            current(MergedMiddlewareScope(coreScope, next))
        }

    override suspend fun dispatch(action: Action): Unit = dispatchPipeline(action)
}