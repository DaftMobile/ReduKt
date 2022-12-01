package co.redukt.test.store

import co.redukt.core.Action
import co.redukt.core.closure.DispatchClosure
import co.redukt.core.closure.EmptyDispatchClosure
import co.redukt.core.store.Store
import co.redukt.test.assertions.ActionsAssertScope
import co.redukt.test.tools.SpyingDispatchScope
import kotlinx.coroutines.flow.MutableStateFlow

public class TestStore<State>(
    initialState: State,
    override var closure: DispatchClosure = EmptyDispatchClosure
) : Store<State> {

    override val state: MutableStateFlow<State> = MutableStateFlow(initialState)

    private val scope = SpyingDispatchScope(state::value, ::closure)

    override fun dispatch(action: Action): Unit = scope.dispatch(action)

    override val currentState: State get() = state.value

    public fun test(block: ActionsAssertScope.() -> Unit): Unit = scope.block()
}
