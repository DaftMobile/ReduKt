package com.daftmobile.redukt.test.tools

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.context.DispatchContext
import com.daftmobile.redukt.core.scope.DispatchScope
import com.daftmobile.redukt.test.assertions.ActionsAssertScope

public class SpyingDispatchScope<State>(
    private val stateProvider: () -> State,
    private val contextProvider: () -> DispatchContext,
) : DispatchScope<State>, ActionsAssertScope {

    override val dispatchContext: DispatchContext get() = contextProvider()
    override val state: State get() = stateProvider()

    private val _history = mutableListOf<Action>()
    override val history: List<Action> = _history

    override val pipeline: Queue<Action> = emptyQueue()

    override suspend fun dispatch(action: Action) {
        _history.add(action)
        pipeline.push(action)
    }
}
