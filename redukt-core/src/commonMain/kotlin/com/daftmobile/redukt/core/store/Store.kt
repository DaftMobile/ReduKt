package com.daftmobile.redukt.core.store

import com.daftmobile.redukt.core.*
import com.daftmobile.redukt.core.closure.DispatchClosure
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
    override val closure: DispatchClosure
) : Store<State> {
    override val state = MutableStateFlow(initialState)

    override val currentState: State get() = state.value

    private val dispatchPipeline: DispatchFunction by lazy {
        middlewares
            .reversed()
            .fold(updateState) { next, current ->
                current(MergedMiddlewareScope(coreScope, next))
            }
    }

    private val coreScope = CoreDispatchScope(closure, dispatchPipeline, this.state::value)

    private val updateState: DispatchFunction = { action -> state.value = reducer(state.value, action) }
    override fun dispatch(action: Action): Unit = coreScope.dispatch(action)

    @DelicateReduKtApi
    override fun dispatch(action: Action, closure: DispatchClosure) = coreScope.dispatch(action, closure)
}