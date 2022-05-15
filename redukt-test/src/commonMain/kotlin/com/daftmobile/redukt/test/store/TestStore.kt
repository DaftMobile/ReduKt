package com.daftmobile.redukt.test.store

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.context.DispatchContext
import com.daftmobile.redukt.core.context.EmptyDispatchContext
import com.daftmobile.redukt.core.store.Store
import com.daftmobile.redukt.test.assertions.ActionsAssertScope
import com.daftmobile.redukt.test.tools.SpyingDispatchScope
import kotlinx.coroutines.flow.MutableStateFlow

class TestStore<State>(
    initialState: State,
    override var dispatchContext: DispatchContext = EmptyDispatchContext
) : Store<State> {

    override val state = MutableStateFlow(initialState)

    private val scope = SpyingDispatchScope(state::value, ::dispatchContext)

    override fun dispatch(action: Action) = scope.dispatch(action)

    fun test(block: ActionsAssertScope.() -> Unit) = scope.block()
}