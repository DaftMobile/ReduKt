package com.github.lupuuss.redukt.core.scope

import com.github.lupuuss.redukt.core.Action
import com.github.lupuuss.redukt.core.context.DispatchContext

internal class CoreDispatchScope<State>(
    override val dispatchContext: DispatchContext,
    private val dispatchFunction: (Action) -> Unit,
    private val getState: () -> State,
) : DispatchScope<State> {

    override val state: State get() = getState()

    override fun dispatch(action: Action) = dispatchFunction(action)
}