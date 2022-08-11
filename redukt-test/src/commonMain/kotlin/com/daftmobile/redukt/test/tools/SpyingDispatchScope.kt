package com.daftmobile.redukt.test.tools

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.closure.DispatchClosure
import com.daftmobile.redukt.core.DispatchScope
import com.daftmobile.redukt.test.assertions.ActionsAssertScope

public class SpyingDispatchScope<State>(
    private val stateProvider: () -> State,
    private val closureProvider: () -> DispatchClosure,
) : DispatchScope<State>, ActionsAssertScope {

    override val closure: DispatchClosure get() = closureProvider()
    override val state: State get() = stateProvider()

    private val _history = mutableListOf<Action>()
    override val history: List<Action> = _history

    override val pipeline: Queue<Action> = emptyQueue()

    override suspend fun dispatch(action: Action) {
        _history.add(action)
        pipeline.push(action)
    }
}
