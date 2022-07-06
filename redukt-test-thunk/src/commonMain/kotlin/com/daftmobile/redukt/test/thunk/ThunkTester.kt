package com.daftmobile.redukt.test.thunk

import com.daftmobile.redukt.core.context.DispatchContext
import com.daftmobile.redukt.core.context.EmptyDispatchContext
import com.daftmobile.redukt.test.assertions.ActionsAssertScope
import com.daftmobile.redukt.test.tools.SpyingDispatchScope
import com.daftmobile.redukt.thunk.Thunk
import kotlinx.coroutines.test.runTest


suspend fun <State> Thunk<State>.testExecute(
    state: State,
    context: DispatchContext = EmptyDispatchContext,
    testBlock: ActionsAssertScope.() -> Unit
) {
    SpyingDispatchScope(stateProvider = { state }, contextProvider = { context }).run {
        execute()
        testBlock()
    }
}

fun <State> Thunk<State>.runTestExecute(
    state: State,
    context: DispatchContext = EmptyDispatchContext,
    testBlock: ActionsAssertScope.() -> Unit
) = runTest { testExecute(state, context, testBlock) }
