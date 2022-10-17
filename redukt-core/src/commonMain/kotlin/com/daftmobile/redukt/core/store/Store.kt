package com.daftmobile.redukt.core.store

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.DispatchFunction
import com.daftmobile.redukt.core.DispatchScope
import com.daftmobile.redukt.core.Reducer
import com.daftmobile.redukt.core.closure.CoreLocalClosure
import com.daftmobile.redukt.core.closure.DispatchClosure
import com.daftmobile.redukt.core.coroutines.DispatchCoroutineScope
import com.daftmobile.redukt.core.coroutines.EmptyForegroundJobRegistry
import com.daftmobile.redukt.core.middleware.MergedMiddlewareScope
import com.daftmobile.redukt.core.middleware.Middleware
import kotlinx.coroutines.MainScope
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

    override val closure: DispatchClosure = EmptyForegroundJobRegistry()
        .plus(CoreLocalClosure(::closure::get))
        .plus(DispatchCoroutineScope(MainScope()))
        .plus(initialClosure)

    override val state = MutableStateFlow(initialState)

    private val updateState: DispatchFunction = { action ->
        guard.stateChange {
            state.value = reducer(state.value, action)
        }
    }

    private val guard = StoreGuard(
        stateAccessor = state::value,
        dispatchPipelineProvider = {
            middlewares
                .reversed()
                .fold(updateState) { next, current ->
                    current(MergedMiddlewareScope(this, next))
                }
        }
    )

    init {
        guard.inflate()
    }

    override val currentState: State get() = guard.getState()
    override fun dispatch(action: Action): Unit = guard.dispatchFunction(action)
}

private class StoreGuard<State>(
    private val stateAccessor: () -> State,
    dispatchPipelineProvider: () -> DispatchFunction,
) {

    var dispatchFunction: DispatchFunction = UninitializedDispatch
        private set
    var getState: () -> State = stateAccessor
        private set

    private val dispatchPipeline by lazy(dispatchPipelineProvider)

    fun inflate() {
        dispatchFunction = dispatchPipeline
    }

    inline fun stateChange(stateChange: () -> Unit) {
        getState = UnsafeStateAccess
        dispatchFunction = UnsafeDispatch
        stateChange()
        getState = stateAccessor
        dispatchFunction = dispatchPipeline
    }
}

private val UninitializedDispatch: DispatchFunction = { throw UninitializedDispatchException() }
private val UnsafeDispatch: DispatchFunction = { throw UnsafeDispatchException() }
private val UnsafeStateAccess: () -> Nothing = { throw UnsafeStateAccessException() }

internal class UninitializedDispatchException :
    IllegalStateException("Calling dispatch during middleware creation is illegal!")

internal class UnsafeDispatchException : IllegalStateException("Calling dispatch during state update is illegal!")

internal class UnsafeStateAccessException : IllegalStateException("Accessing state during state update is illegal!")