package com.daftmobile.redukt.test.thunk

import com.daftmobile.redukt.core.closure.DispatchClosure
import com.daftmobile.redukt.core.closure.EmptyDispatchClosure
import com.daftmobile.redukt.core.withLocalScope
import com.daftmobile.redukt.test.assertions.ActionsAssertScope
import com.daftmobile.redukt.test.tools.SpyingDispatchScope
import com.daftmobile.redukt.thunk.CoThunkApi
import com.daftmobile.redukt.thunk.ThunkAction
import com.daftmobile.redukt.thunk.ThunkApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest

public fun <State> ThunkApi<State>.testExecute(
    state: State,
    closure: DispatchClosure = EmptyDispatchClosure,
    testBlock: ActionsAssertScope.() -> Unit
) {
    SpyingDispatchScope(stateProvider = { state }, closureProvider = { closure }).run {
        withLocalScope {
            execute()
        }
        testBlock()
    }
}

public suspend fun <State> CoThunkApi<State>.testExecute(
    state: State,
    closure: DispatchClosure = EmptyDispatchClosure,
    testBlock: suspend ActionsAssertScope.() -> Unit
) {
    SpyingDispatchScope(stateProvider = { state }, closureProvider = { closure }).run {
        withLocalScope {
            execute()
        }
        testBlock()
    }
}

@ExperimentalCoroutinesApi
public fun <State> CoThunkApi<State>.runTestExecute(
    state: State,
    closure: DispatchClosure = EmptyDispatchClosure,
    testBlock: suspend ActionsAssertScope.() -> Unit
): TestResult = runTest { testExecute(state, closure, testBlock) }
