package dev.redukt.test.tools

import dev.redukt.core.Action
import dev.redukt.core.DispatchScope
import dev.redukt.core.closure.DispatchClosure
import dev.redukt.test.assertions.ActionsAssertScope

public class SpyingDispatchScope<out State>(
    private val stateProvider: () -> State,
    private val closureProvider: () -> DispatchClosure,
) : DispatchScope<State>, ActionsAssertScope {

    override val closure: DispatchClosure get() = closureProvider()
    override val currentState: State get() = stateProvider()

    private val _history = mutableListOf<Action>()
    override val history: List<Action> = _history

    override val pipeline: Queue<Action> = emptyQueue()

    override fun dispatch(action: Action) {
        _history.add(action)
        pipeline.push(action)
    }
}
