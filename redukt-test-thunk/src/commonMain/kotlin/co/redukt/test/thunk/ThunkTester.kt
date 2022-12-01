package co.redukt.test.thunk

import co.redukt.core.closure.DispatchClosure
import co.redukt.core.closure.EmptyDispatchClosure
import co.redukt.test.assertions.ActionsAssertScope
import co.redukt.test.tools.ImmutableLocalClosure
import co.redukt.test.tools.MockForegroundJobRegistry
import co.redukt.test.tools.SpyingDispatchScope
import co.redukt.thunk.CoThunkApi
import co.redukt.thunk.ThunkApi

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
