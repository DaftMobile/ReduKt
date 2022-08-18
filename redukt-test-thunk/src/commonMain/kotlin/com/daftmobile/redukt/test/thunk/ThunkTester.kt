package com.daftmobile.redukt.test.thunk

import com.daftmobile.redukt.core.closure.DispatchClosure
import com.daftmobile.redukt.core.closure.EmptyDispatchClosure
import com.daftmobile.redukt.test.assertions.ActionsAssertScope
import com.daftmobile.redukt.test.tools.SpyingDispatchScope
import com.daftmobile.redukt.thunk.Thunk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest

public suspend fun <State> Thunk<State>.testExecute(
    state: State,
    closure: DispatchClosure = EmptyDispatchClosure,
    testBlock: suspend ActionsAssertScope.() -> Unit
) {
    SpyingDispatchScope(stateProvider = { state }, closureProvider = { closure }).run {
        execute()
        testBlock()
    }
}
@ExperimentalCoroutinesApi
public fun <State> Thunk<State>.runTestExecute(
    state: State,
    closure: DispatchClosure = EmptyDispatchClosure,
    testBlock: suspend ActionsAssertScope.() -> Unit
): TestResult = runTest { testExecute(state, closure, testBlock) }
