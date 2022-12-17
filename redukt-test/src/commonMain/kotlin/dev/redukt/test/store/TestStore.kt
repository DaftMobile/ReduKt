package dev.redukt.test.store

import dev.redukt.core.Action
import dev.redukt.core.DispatchFunction
import dev.redukt.core.closure.DispatchClosure
import dev.redukt.core.closure.EmptyDispatchClosure
import dev.redukt.core.store.Store
import dev.redukt.test.assertions.ActionsAssertScope
import dev.redukt.test.tools.MockDispatchScope
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * ReduKt store for testing purposes.
 */
public interface TestStore<State> : Store<State> {

    override val state: MutableStateFlow<State>

    override var closure: DispatchClosure

    /**
     * Sets a [dispatchFunction] that will be called on every dispatch. Each call replaces previously provided [dispatchFunction].
     */
    public fun onDispatch(dispatchFunction: DispatchFunction)

    /**
     * Runs test with [block] that verifies dispatched actions with this store.
     */
    public fun test(block: ActionsAssertScope.() -> Unit)
}

public fun <State> TestStore(
    initialState: State,
    initialClosure: DispatchClosure = EmptyDispatchClosure,
): TestStore<State> = TestStoreImpl(initialState, initialClosure)

private class TestStoreImpl<State>(
    initialState: State,
    override var closure: DispatchClosure
) : TestStore<State> {

    override val state: MutableStateFlow<State> = MutableStateFlow(initialState)

    var dispatchFunction: DispatchFunction = { }
    override fun onDispatch(dispatchFunction: DispatchFunction) {
        this.dispatchFunction = dispatchFunction
    }

    private val scope = MockDispatchScope(
        stateProvider = state::value,
        closureProvider = ::closure,
        dispatchFunction = { dispatchFunction(it) })

    override fun dispatch(action: Action): Unit = scope.dispatch(action)

    override val currentState: State get() = state.value

    override fun test(block: ActionsAssertScope.() -> Unit): Unit = scope.block()
}
