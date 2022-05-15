package com.daftmobile.redukt.test.tools

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.context.DispatchContext
import com.daftmobile.redukt.core.scope.DispatchScope
import com.daftmobile.redukt.test.assertions.ActionsAssertScope

class SpyingDispatchScope<State>(
    private val stateProvider: () -> State,
    private val contextProvider: () -> DispatchContext,
) : DispatchScope<State>, ActionsAssertScope {

    override val dispatchContext: DispatchContext get() = contextProvider()
    override val state: State get() = stateProvider()

    override val history = mutableListOf<Action>()
    override val pipeline = emptyQueue<Action>()

    override fun dispatch(action: Action) {
        history.add(action)
        pipeline.push(action)
    }
}
