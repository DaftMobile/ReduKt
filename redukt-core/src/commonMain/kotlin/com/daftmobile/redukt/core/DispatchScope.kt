package com.daftmobile.redukt.core

import com.daftmobile.redukt.core.context.DispatchContext

public interface DispatchScope<State> : ActionDispatcher {

    public val state: State
}

internal class CoreDispatchScope<State>(
    override val dispatchContext: DispatchContext,
    private val dispatchFunction: DispatchFunction,
    private val getState: () -> State,
) : DispatchScope<State> {

    override val state: State get() = getState()

    override suspend fun dispatch(action: Action) = dispatchFunction(action)
}