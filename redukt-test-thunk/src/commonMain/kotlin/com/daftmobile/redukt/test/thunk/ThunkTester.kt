package com.daftmobile.redukt.test.thunk

import com.daftmobile.redukt.core.context.DispatchContext
import com.daftmobile.redukt.core.context.EmptyDispatchContext
import com.daftmobile.redukt.test.assertions.ActionsAssertScope
import com.daftmobile.redukt.test.tools.SpyingDispatchScope
import com.daftmobile.redukt.thunk.Thunk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest


public suspend fun <State> Thunk<State>.testExecute(
    state: State,
    context: DispatchContext = EmptyDispatchContext,
    testBlock: suspend ActionsAssertScope.() -> Unit
) {
    SpyingDispatchScope(stateProvider = { state }, contextProvider = { context }).run {
        execute()
        testBlock()
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
public fun <State> Thunk<State>.runTestExecute(
    state: State,
    context: DispatchContext = EmptyDispatchContext,
    testBlock: suspend ActionsAssertScope.() -> Unit
): TestResult = runTest { testExecute(state, context, testBlock) }
