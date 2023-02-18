package com.daftmobile.redukt.core.store

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.DispatchFunction
import com.daftmobile.redukt.core.DispatchScope
import com.daftmobile.redukt.core.Reducer
import com.daftmobile.redukt.core.closure.DispatchClosure
import com.daftmobile.redukt.core.coroutines.DispatchCoroutineScope
import com.daftmobile.redukt.core.coroutines.runningCoroutinesClosure
import com.daftmobile.redukt.core.middleware.MergedMiddlewareScope
import com.daftmobile.redukt.core.middleware.Middleware
import com.daftmobile.redukt.core.store.select.SelectStateFlowProvider
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * This is an equivalent of [Redux store](https://redux.js.org/tutorials/fundamentals/part-4-store#redux-store).
 * However, there are a few differences:
 * * Instead of [getState](https://redux.js.org/api/store#getState) there is a [currentState] property
 * that returns current state.
 * * State changes are received by collecting [state].
 * * Contains [closure] field that provides a mechanism to inject objects to the store and make them
 * available to middlewares.
 *
 * To create an instance use [buildStore] (recommended) or Store() function.
 */
public interface Store<out State> : DispatchScope<State> {

    public val state: StateFlow<State>
}

/**
 * Creates a [Store]. This is an alternative to [buildStore], which is a recommended way of creating a [Store].
 *
 *  Use cases for this function:
 * * A custom builder
 * * A custom [Store] implementation that delegates to this one
 */
public fun <State> Store(
    initialState: State,
    reducer: Reducer<State>,
    middlewares: List<Middleware<State>>,
    initialClosure: DispatchClosure,
): Store<State> = StoreImpl(initialState, reducer, middlewares, initialClosure)

private class StoreImpl<State>(
    initialState: State,
    private val reducer: Reducer<State>,
    middlewares: List<Middleware<State>>,
    initialClosure: DispatchClosure,
) : Store<State> {

    override val closure: DispatchClosure = runningCoroutinesClosure()
        .plus(DispatchCoroutineScope(MainScope()))
        .plus(SelectStateFlowProvider())
        .plus(initialClosure)

    override val state = MutableStateFlow(initialState)

    private val updateState: DispatchFunction = { action ->
        guard.stateChange {
            state.value = reducer(state.value, action)
        }
    }

    private val guard = DispatchGuard(
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

    override val currentState: State get() = state.value
    override fun dispatch(action: Action): Unit = guard.dispatchFunction(action)
}

private class DispatchGuard(
    dispatchPipelineProvider: () -> DispatchFunction,
) {

    var dispatchFunction: DispatchFunction = UninitializedDispatch
        private set

    private val dispatchPipeline by lazy(dispatchPipelineProvider)

    fun inflate() {
        dispatchFunction = dispatchPipeline
    }

    inline fun stateChange(stateChange: () -> Unit) {
        dispatchFunction = UnsafeDispatch
        stateChange()
        dispatchFunction = dispatchPipeline
    }
}

private val UninitializedDispatch: DispatchFunction = { throw UninitializedDispatchException() }
private val UnsafeDispatch: DispatchFunction = { throw UnsafeDispatchException() }

internal class UninitializedDispatchException :
    IllegalStateException("Calling dispatch during middleware creation is illegal!")

internal class UnsafeDispatchException : IllegalStateException("Calling dispatch during state update is illegal!")
