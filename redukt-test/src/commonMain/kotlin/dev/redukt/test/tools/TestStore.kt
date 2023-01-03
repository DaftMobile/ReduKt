package dev.redukt.test.tools

import dev.redukt.core.Action
import dev.redukt.core.closure.DispatchClosure
import dev.redukt.core.closure.EmptyDispatchClosure
import dev.redukt.core.store.Store
import dev.redukt.test.MutableDispatchScope
import dev.redukt.test.assertions.ActionsAssertScope
import dev.redukt.test.assertions.assertNoMoreActions
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * ReduKt store for testing purposes that registers dispatched actions.
 */
public interface TestStore<State> : MutableDispatchScope<State>, Store<State> {

    override val state: MutableStateFlow<State>

    /**
     * If it is true, every [test] call must process all [ActionsAssertScope.unverified] actions.
     */
    public val strict: Boolean

    /**
     * Runs test with [block] that verifies dispatched actions with this store.
     *
     * @param strict if it is not null, overwrites [strict] param value for this test call.
     */
    public fun test(strict: Boolean? = null, block: ActionsAssertScope.() -> Unit)
}

/**
 * Creates a [TestStore] with [initialState], [initialClosure] and [strict].
 * If [strict] is true, every [TestStore.test] must verify every dispatched action.
 */
public fun <State> TestStore(
    initialState: State,
    initialClosure: DispatchClosure = EmptyDispatchClosure,
    initialOnDispatch: MutableDispatchScope<State>.(Action) -> Unit = { },
    strict: Boolean = true,
): TestStore<State> = TestStoreImpl(initialState, initialClosure, initialOnDispatch, strict)

private class TestStoreImpl<State>(
    initialState: State,
    initialClosure: DispatchClosure,
    initialOnDispatch: MutableDispatchScope<State>.(Action) -> Unit,
    override val strict: Boolean
) : TestStore<State> {

    override val state: MutableStateFlow<State> = MutableStateFlow(initialState)

    private val scope = TestDispatchScope(Unit, initialClosure, initialOnDispatch = { initialOnDispatch(it) })
    override fun onDispatch(block: MutableDispatchScope<State>.(Action) -> Unit) = scope.onDispatch { block(it) }

    override fun dispatch(action: Action): Unit = scope.dispatch(action)

    override var currentState: State
        get() = state.value
        set(value) {
            state.value = value
        }

    override var closure: DispatchClosure
        get() = scope.closure
        set(value) {
            scope.closure = value
        }

    override fun test(strict: Boolean?, block: ActionsAssertScope.() -> Unit) {
        scope.block()
        if (strict ?: this.strict) scope.assertNoMoreActions()
    }
}
