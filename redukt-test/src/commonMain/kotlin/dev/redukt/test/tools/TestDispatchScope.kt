package dev.redukt.test.tools

import dev.redukt.core.Action
import dev.redukt.core.DispatchScope
import dev.redukt.core.closure.DispatchClosure
import dev.redukt.core.closure.EmptyDispatchClosure
import dev.redukt.test.MutableDispatchScope
import dev.redukt.test.assertions.ActionsAssertScope

/**
 * A [DispatchScope] for testing purposes that registers dispatched actions.
 */
public interface TestDispatchScope<State> : MutableDispatchScope<State>, DispatchScope<State>, ActionsAssertScope

/**
 * Creates a [TestDispatchScope] with [initialState], [initialClosure] and [initialOnDispatch].
 */
public fun <State> TestDispatchScope(
    initialState: State,
    initialClosure: DispatchClosure = EmptyDispatchClosure,
    initialOnDispatch: MutableDispatchScope<State>.(Action) -> Unit = { },
): TestDispatchScope<State> = TestDispatchScopeImpl(initialState, initialClosure, initialOnDispatch)

private class TestDispatchScopeImpl<State>(
    initialState: State,
    initialClosure: DispatchClosure,
    private var onDispatch: MutableDispatchScope<State>.(Action) -> Unit,
) : TestDispatchScope<State> {

    override var closure: DispatchClosure = SkipCoroutinesClosure() + initialClosure

    override var currentState: State = initialState

    override fun onDispatch(block: MutableDispatchScope<State>.(Action) -> Unit) {
        this.onDispatch = block
    }

    private val _history = mutableListOf<Action>()
    override val history: List<Action> = _history

    override val unverified: Queue<Action> = emptyQueue()

    override fun dispatch(action: Action) {
        _history.add(action)
        unverified.push(action)
        onDispatch(action)
    }

    override fun clearActionsHistory() {
        _history.clear()
        unverified.pullEach { }
    }
}
