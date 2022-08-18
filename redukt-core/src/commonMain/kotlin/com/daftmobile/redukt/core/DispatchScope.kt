package com.daftmobile.redukt.core

import com.daftmobile.redukt.core.closure.DispatchClosure

public interface DispatchScope<State> : ClosureScope {

    public val currentState: State

    public fun dispatch(action: Action)

    @DelicateReduKtApi
    public fun dispatch(action: Action, closure: DispatchClosure)
}

public fun DispatchScope<*>.dispatchIfPresent(action: Action?) {
    action?.let { dispatch(it) }
}

@InternalReduKtApi
public class CoreDispatchScope<State>(
    override val closure: DispatchClosure,
    private val dispatchFunction: DispatchFunction,
    private val getState: () -> State,
) : DispatchScope<State> {

    override val currentState: State get() = getState()

    private val defaultLocalScope = closure.asLocalScope()

    override fun dispatch(action: Action): Unit = defaultLocalScope.dispatchFunction(action)

    override fun dispatch(action: Action, closure: DispatchClosure): Unit = dispatchFunction
        .invokeWithMergedClosure(this.closure, closure, action)
}