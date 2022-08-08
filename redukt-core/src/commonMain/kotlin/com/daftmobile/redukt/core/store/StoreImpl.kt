package com.daftmobile.redukt.core.store

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.Middleware
import com.daftmobile.redukt.core.Reducer
import com.daftmobile.redukt.core.context.DispatchContext
import com.daftmobile.redukt.core.context.element.withDispatchCoroutineContext
import com.daftmobile.redukt.core.middleware.MergedMiddlewareScope
import com.daftmobile.redukt.core.scope.CoreDispatchScope
import kotlinx.coroutines.flow.MutableStateFlow

internal class StoreImpl<State>(
    initialState: State,
    private val reducer: Reducer<State>,
    middlewares: List<Middleware<State>>,
    override val dispatchContext: DispatchContext
) : Store<State> {
    override val state = MutableStateFlow(initialState)

    private val coreScope = CoreDispatchScope(dispatchContext, this::dispatch, this.state::value)

    private val updateState: suspend (Action) -> Unit = { action ->
        coreScope.withDispatchCoroutineContext {
            state.value = reducer(state.value, action)
        }
    }
    private val dispatchPipeline: suspend (Action) -> Unit = middlewares
        .reversed()
        .fold(updateState) { next, current ->
            current(MergedMiddlewareScope(coreScope, next))
        }

    override suspend fun dispatch(action: Action): Unit = dispatchPipeline(action)
}