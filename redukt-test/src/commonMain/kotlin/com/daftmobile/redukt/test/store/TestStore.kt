package com.daftmobile.redukt.test.store

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.closure.DispatchClosure
import com.daftmobile.redukt.core.closure.EmptyDispatchClosure
import com.daftmobile.redukt.core.store.Store
import com.daftmobile.redukt.test.assertions.ActionsAssertScope
import com.daftmobile.redukt.test.tools.SpyingDispatchScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

public class TestStore<State>(
    initialState: State,
    override var closure: DispatchClosure = EmptyDispatchClosure
) : Store<State> {

    override val state: StateFlow<State> = MutableStateFlow(initialState)

    private val scope = SpyingDispatchScope(state::value, ::closure)

    override suspend fun dispatch(action: Action): Unit = scope.dispatch(action)

    override val currentState: State get() = state.value

    public fun test(block: ActionsAssertScope.() -> Unit): Unit = scope.block()
}