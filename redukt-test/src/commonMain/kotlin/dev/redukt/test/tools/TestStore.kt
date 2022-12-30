package dev.redukt.test.tools

import dev.redukt.core.Action
import dev.redukt.core.DispatchFunction
import dev.redukt.core.closure.DispatchClosure
import dev.redukt.core.closure.EmptyDispatchClosure
import dev.redukt.core.store.Store
import dev.redukt.test.assertions.ActionsAssertScope
import dev.redukt.test.assertions.assertNoMoreActions
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * ReduKt store for testing purposes that registers dispatched actions.
 */
public interface TestStore<State> : Store<State> {

    override val state: MutableStateFlow<State>

    override var closure: DispatchClosure

    /**
     * If it is true, every [test] call must process all [ActionsAssertScope.unverified] actions.
     */
    public val strict: Boolean

    /**
     * Sets a [dispatchFunction] that will be called on every dispatch. Each call replaces previously provided [dispatchFunction].
     */
    public fun onDispatch(dispatchFunction: DispatchFunction)

    /**
     * Runs test with [block] that verifies dispatched actions with this store.
     *
     * @param strict if it is not null, overwrites [strict] param value for this test call.
     */
    public fun test(strict: Boolean? = null, block: ActionsAssertScope.() -> Unit)

    /**
     * Clears dispatched actions.
     */
    public fun reset()
}

/**
 * Creates a [TestStore] with [initialState], [initialClosure] and [strict].
 * If [strict] is true, every [TestStore.test] must verify every dispatched action.
 */
public fun <State> TestStore(
    initialState: State,
    initialClosure: DispatchClosure = EmptyDispatchClosure,
    strict: Boolean = true,
): TestStore<State> = TestStoreImpl(initialState, initialClosure, strict)

private class TestStoreImpl<State>(
    initialState: State,
    override var closure: DispatchClosure,
    override val strict: Boolean
) : TestStore<State> {

    override val state: MutableStateFlow<State> = MutableStateFlow(initialState)

    var dispatchFunction: DispatchFunction = { }
    override fun onDispatch(dispatchFunction: DispatchFunction) {
        this.dispatchFunction = dispatchFunction
    }

    private val scope = TestDispatchScope(
        stateProvider = state::value,
        closureProvider = ::closure,
        dispatchFunction = { dispatchFunction(it) }
    )

    override fun dispatch(action: Action): Unit = scope.dispatch(action)

    override val currentState: State get() = state.value

    override fun test(strict: Boolean?, block: ActionsAssertScope.() -> Unit) {
        scope.block()
        if (strict ?: this.strict) scope.assertNoMoreActions()
    }

    override fun reset() {
        scope.reset()
    }
}
