package dev.redukt.test.store

import dev.redukt.core.Action
import dev.redukt.core.closure.DispatchClosure
import dev.redukt.core.closure.EmptyDispatchClosure
import dev.redukt.core.store.Store
import dev.redukt.test.assertions.ActionsAssertScope
import dev.redukt.test.tools.SpyingDispatchScope
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
