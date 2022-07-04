package com.daftmobile.redukt.core.store

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.Reducer
import com.daftmobile.redukt.core.context.DispatchContext
import com.daftmobile.redukt.core.context.EmptyDispatchContext
import com.daftmobile.redukt.core.middleware.Middleware
import com.daftmobile.redukt.core.middleware.MiddlewareStatus
import com.daftmobile.redukt.core.scope.CoreDispatchScope
import kotlinx.coroutines.flow.MutableStateFlow

internal class StoreImpl<State>(
    initialState: State,
    private val reducer: Reducer<State>,
    private val middlewares: List<Middleware<State>> = emptyList(),
    override val dispatchContext: DispatchContext = EmptyDispatchContext
) : Store<State> {
    override val state = MutableStateFlow(initialState)

    private val scope = CoreDispatchScope(dispatchContext, this::dispatch, this.state::value)

    override suspend fun dispatch(action: Action) {
        var status: MiddlewareStatus = MiddlewareStatus.Next(action)
        for (middleware in middlewares) {
            status = middleware(scope, action)
            if (status is MiddlewareStatus.Consumed) break
        }
        if (status is MiddlewareStatus.Next) state.value = reducer(state.value, action)
    }
}