package dev.redukt.test.thunk

import dev.redukt.core.closure.DispatchClosure
import dev.redukt.core.closure.EmptyDispatchClosure
import dev.redukt.test.assertions.ActionsAssertScope
import dev.redukt.test.tools.ImmutableLocalClosure
import dev.redukt.test.tools.MockForegroundJobRegistry
import dev.redukt.test.tools.SpyingDispatchScope
import dev.redukt.thunk.CoThunkApi
import dev.redukt.thunk.ThunkApi

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
