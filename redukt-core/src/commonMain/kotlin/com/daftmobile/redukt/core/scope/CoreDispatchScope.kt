package com.daftmobile.redukt.core.scope

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.context.DispatchContext

internal class CoreDispatchScope<State>(
    override val dispatchContext: DispatchContext,
    private val dispatchFunction: (Action) -> Unit,
    private val getState: () -> State,
) : DispatchScope<State> {

    override val state: State get() = getState()

    override fun dispatch(action: Action) = dispatchFunction(action)
}