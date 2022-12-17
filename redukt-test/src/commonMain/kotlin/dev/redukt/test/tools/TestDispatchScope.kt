package dev.redukt.test.tools

import dev.redukt.core.Action
import dev.redukt.core.DispatchFunction
import dev.redukt.core.DispatchScope
import dev.redukt.core.closure.DispatchClosure
import dev.redukt.core.closure.EmptyDispatchClosure
import dev.redukt.test.assertions.ActionsAssertScope

/**
 * A DispatchScope for testing purposes that registers dispatched actions.
 */
public interface TestDispatchScope<out State> :  DispatchScope<State>, ActionsAssertScope {

    /**
     * Clears [unverified] and [history].
     */
    public fun reset()
}

/**
 * Creates a [TestDispatchScope] that provides state and closure from [stateProvider] and [closureProvider].
 * On every dispatch [dispatchFunction] is called.
 */
public fun <State> TestDispatchScope(
    stateProvider: () -> State,
    closureProvider: () -> DispatchClosure = { EmptyDispatchClosure },
    dispatchFunction: DispatchFunction = { },
): TestDispatchScope<State> = TestDispatchScopeImpl(stateProvider, closureProvider, dispatchFunction)

private class TestDispatchScopeImpl<out State>(
    private val stateProvider: () -> State,
    private val closureProvider: () -> DispatchClosure,
    private val dispatchFunction: DispatchFunction,
) : TestDispatchScope<State> {

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
