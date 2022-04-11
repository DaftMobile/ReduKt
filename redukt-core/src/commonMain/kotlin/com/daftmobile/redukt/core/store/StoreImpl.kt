package com.daftmobile.redukt.core.store

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.Reducer
import com.daftmobile.redukt.core.context.DispatchContext
import com.daftmobile.redukt.core.context.EmptyDispatchContext
import com.daftmobile.redukt.core.middleware.Middleware
import com.daftmobile.redukt.core.middleware.processWith
import com.daftmobile.redukt.core.scope.CoreDispatchScope
import kotlinx.coroutines.flow.MutableStateFlow

internal class StoreImpl<State>(
    initialState: State,
    private val reducer: Reducer<State>,
    private val middlewares: List<Middleware<State>> = emptyList(),
    context: DispatchContext = EmptyDispatchContext
) : Store<State> {
    override val state = MutableStateFlow(initialState)

    private val scope = CoreDispatchScope(context, this::dispatch, this.state::value)

    override fun dispatch(action: Action) {
        var status = Middleware.Status.Passed
        for (middleware in middlewares) {
            status = middleware.processWith(scope, action)
            if (status == Middleware.Status.Consumed) break
        }
        if (status == Middleware.Status.Passed) state.value = reducer(state.value, action)
    }
}