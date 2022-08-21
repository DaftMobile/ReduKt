package com.daftmobile.redukt.core

import com.daftmobile.redukt.core.closure.DispatchClosure

public interface DispatchScope<State> {

    public val closure: DispatchClosure

    public val currentState: State

    public fun dispatch(action: Action)
}

public fun DispatchScope<*>.dispatchIfPresent(action: Action?) {
    action?.let { dispatch(it) }
}

internal class CoreDispatchScope<State>(
    override val closure: DispatchClosure,
    private val dispatchFunction: DispatchFunction,
    private val getState: () -> State,
) : DispatchScope<State> {

    override val currentState: State get() = getState()

    override fun dispatch(action: Action): Unit = dispatchFunction(action)
}
