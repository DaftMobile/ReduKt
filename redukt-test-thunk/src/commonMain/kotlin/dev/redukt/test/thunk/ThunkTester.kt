package dev.redukt.test.thunk

import dev.redukt.core.closure.DispatchClosure
import dev.redukt.core.closure.EmptyDispatchClosure
import dev.redukt.test.assertions.ActionsAssertScope
import dev.redukt.test.assertions.expectNoMoreActions
import dev.redukt.test.tools.ImmutableLocalClosure
import dev.redukt.test.tools.MockForegroundJobRegistry
import dev.redukt.test.tools.SpyingDispatchScope
import dev.redukt.thunk.CoThunkAction
import dev.redukt.thunk.ThunkAction

public fun <State> ThunkAction<State>.testExecute(
    state: State,
    closure: DispatchClosure = EmptyDispatchClosure,
    strict: Boolean = true,
    testBlock: ActionsAssertScope.() -> Unit
) {
    val resultingClosure = MockForegroundJobRegistry() + closure
    SpyingDispatchScope(
        stateProvider = { state },
        closureProvider = { ImmutableLocalClosure { resultingClosure } + resultingClosure }
    ).run {
        execute()
        testBlock()
        if (strict) expectNoMoreActions()
    }
}

public suspend fun <State> CoThunkAction<State>.testExecute(
    state: State,
    closure: DispatchClosure = EmptyDispatchClosure,
    strict: Boolean = true,
    testBlock: suspend ActionsAssertScope.() -> Unit
) {
    val initialClosure = MockForegroundJobRegistry() + closure
    SpyingDispatchScope(
        stateProvider = { state },
        closureProvider = { ImmutableLocalClosure { initialClosure } + initialClosure }
    ).run {
        execute()
        testBlock()
        if (strict) expectNoMoreActions()
    }
}
