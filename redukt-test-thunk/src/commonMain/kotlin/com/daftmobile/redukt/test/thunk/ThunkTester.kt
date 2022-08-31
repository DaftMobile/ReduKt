package com.daftmobile.redukt.test.thunk

import com.daftmobile.redukt.core.closure.DispatchClosure
import com.daftmobile.redukt.core.closure.EmptyDispatchClosure
import com.daftmobile.redukt.test.assertions.ActionsAssertScope
import com.daftmobile.redukt.test.tools.ImmutableLocalClosure
import com.daftmobile.redukt.test.tools.MockForegroundJobRegistry
import com.daftmobile.redukt.test.tools.SpyingDispatchScope
import com.daftmobile.redukt.thunk.CoThunkApi
import com.daftmobile.redukt.thunk.ThunkApi

public fun <State> ThunkApi<State>.testExecute(
    state: State,
    closure: DispatchClosure = EmptyDispatchClosure,
    testBlock: ActionsAssertScope.() -> Unit
) {
    val resultingClosure = MockForegroundJobRegistry() + closure
    SpyingDispatchScope(
        stateProvider = { state },
        closureProvider = { ImmutableLocalClosure { resultingClosure } + resultingClosure }
    ).run {
        execute()
        testBlock()
    }
}

public suspend fun <State> CoThunkApi<State>.testExecute(
    state: State,
    closure: DispatchClosure = EmptyDispatchClosure,
    testBlock: suspend ActionsAssertScope.() -> Unit
) {
    val initialClosure = MockForegroundJobRegistry() + closure
    SpyingDispatchScope(
        stateProvider = { state },
        closureProvider = { ImmutableLocalClosure { initialClosure } + initialClosure }
    ).run {
        execute()
        testBlock()
    }
}
