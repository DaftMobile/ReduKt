package com.daftmobile.redukt.core.store

import com.daftmobile.redukt.core.*
import com.daftmobile.redukt.core.closure.DispatchClosure
import com.daftmobile.redukt.core.closure.LocalClosure
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

    override val closure: DispatchClosure = EmptyForegroundJobRegistry() + LocalClosure(::closure::get) + initialClosure
    override val state = MutableStateFlow(initialState)

    override val currentState: State get() = state.value

    private val bridgeDispatch: DispatchFunction = { dispatchPipeline(it) }

    private val coreScope = CoreDispatchScope(closure, bridgeDispatch, this.state::value)

    private val dispatchPipeline: DispatchFunction by lazy {
        middlewares
                .reversed()
                .fold(updateState) { next, current ->
                    current(MergedMiddlewareScope(coreScope, next))
                }
    }

    private val updateState: DispatchFunction = { action -> state.value = reducer(state.value, action) }

    override fun dispatch(action: Action): Unit = coreScope.dispatch(action)
}
