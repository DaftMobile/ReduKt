package dev.redukt.test.tools

import dev.redukt.core.Action
import dev.redukt.core.DispatchFunction
import dev.redukt.core.DispatchScope
import dev.redukt.core.closure.DispatchClosure
import dev.redukt.core.closure.EmptyDispatchClosure
import dev.redukt.test.assertions.ActionsAssertScope

/**
 * A mocked DispatchScope that registers dispatched actions.
 */
public interface MockDispatchScope<out State> :  DispatchScope<State>, ActionsAssertScope {

    /**
     * Clears [unverified] and [history].
     */
    public fun reset()
}

/**
 * Creates a [MockDispatchScope] that provides state and closure from [stateProvider] and [closureProvider].
 * On every dispatch [dispatchFunction] is called.
 */
public fun <State> MockDispatchScope(
    stateProvider: () -> State,
    closureProvider: () -> DispatchClosure = { EmptyDispatchClosure },
    dispatchFunction: DispatchFunction = { },
): MockDispatchScope<State> = MockDispatchScopeImpl(stateProvider, closureProvider, dispatchFunction)

private class MockDispatchScopeImpl<out State>(
    private val stateProvider: () -> State,
    private val closureProvider: () -> DispatchClosure,
    private val dispatchFunction: DispatchFunction,
) : MockDispatchScope<State> {

    override val closure: DispatchClosure get() = closureProvider()
    override val currentState: State get() = stateProvider()

    private val _history = mutableListOf<Action>()
    override val history: List<Action> = _history

    override val unverified: Queue<Action> = emptyQueue()

    override fun dispatch(action: Action) {
        _history.add(action)
        unverified.push(action)
        dispatchFunction(action)
    }

    override fun reset() {
        _history.clear()
        unverified.pullEach {  }
    }
}
