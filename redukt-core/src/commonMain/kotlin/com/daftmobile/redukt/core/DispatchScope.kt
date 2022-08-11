package com.daftmobile.redukt.core

import com.daftmobile.redukt.core.closure.DispatchClosure

public interface DispatchScope<State> : ActionDispatcher {

    public val state: State
}

internal class CoreDispatchScope<State>(
    override val closure: DispatchClosure,
    private val dispatchFunction: DispatchFunction,
    private val getState: () -> State,
) : DispatchScope<State> {

    override val state: State get() = getState()

    override suspend fun dispatch(action: Action) = dispatchFunction(action)
}