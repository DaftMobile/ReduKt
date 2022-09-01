package com.daftmobile.redukt.core.store

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.DispatchFunction
import com.daftmobile.redukt.core.DispatchScope
import com.daftmobile.redukt.core.Reducer
import com.daftmobile.redukt.core.closure.CoreLocalClosure
import com.daftmobile.redukt.core.closure.DispatchClosure
import com.daftmobile.redukt.core.coroutines.EmptyForegroundJobRegistry
import com.daftmobile.redukt.core.middleware.MergedMiddlewareScope
import com.daftmobile.redukt.core.middleware.Middleware
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

public interface Store<State> : DispatchScope<State> {

    public val state: StateFlow<State>
}

internal class StoreImpl<State>(
    initialState: State,
    private val reducer: Reducer<State>,
    middlewares: List<Middleware<State>>,
    initialClosure: DispatchClosure,
) : Store<State> {

    override val closure: DispatchClosure = EmptyForegroundJobRegistry() + CoreLocalClosure(::closure::get) + initialClosure
    override val state = MutableStateFlow(initialState)

    override val currentState: State get() = state.value

    private val updateState: DispatchFunction = { action -> state.value = reducer(state.value, action) }

    private val dispatchPipeline: DispatchFunction = middlewares
                .reversed()
                .fold(updateState) { next, current ->
                    current(MergedMiddlewareScope(this, next))
                }
    override fun dispatch(action: Action): Unit = dispatchPipeline(action)
}
