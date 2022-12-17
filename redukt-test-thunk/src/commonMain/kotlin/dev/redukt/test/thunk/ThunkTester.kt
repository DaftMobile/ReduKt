package dev.redukt.test.thunk

import dev.redukt.core.closure.DispatchClosure
import dev.redukt.core.closure.EmptyDispatchClosure
import dev.redukt.test.assertions.ActionsAssertScope
import dev.redukt.test.assertions.assertNoMoreActions
import dev.redukt.test.tools.TestLocalClosure
import dev.redukt.test.tools.TestForegroundJobRegistry
import dev.redukt.test.tools.TestDispatchScope
import dev.redukt.thunk.CoThunkAction
import dev.redukt.thunk.ThunkAction

public fun <State> ThunkAction<State>.testExecute(
    state: State,
    closure: DispatchClosure = EmptyDispatchClosure,
    strict: Boolean = true,
    testBlock: ActionsAssertScope.() -> Unit
) {
    val resultingClosure = TestForegroundJobRegistry() + closure
    TestDispatchScope(
        stateProvider = { state },
        closureProvider = { TestLocalClosure { resultingClosure } + resultingClosure }
    ).run {
        execute()
        testBlock()
        if (strict) assertNoMoreActions()
    }
}

public suspend fun <State> CoThunkAction<State>.testExecute(
    state: State,
    closure: DispatchClosure = EmptyDispatchClosure,
    strict: Boolean = true,
    testBlock: suspend ActionsAssertScope.() -> Unit
) {
    val initialClosure = TestForegroundJobRegistry() + closure
    TestDispatchScope(
        stateProvider = { state },
        closureProvider = { TestLocalClosure { initialClosure } + initialClosure }
    ).run {
        execute()
        testBlock()
        if (strict) assertNoMoreActions()
    }
}
