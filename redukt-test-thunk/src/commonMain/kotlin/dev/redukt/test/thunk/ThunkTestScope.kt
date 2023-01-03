package dev.redukt.test.thunk

import dev.redukt.core.Action
import dev.redukt.core.closure.DispatchClosure
import dev.redukt.core.closure.EmptyDispatchClosure
import dev.redukt.test.MutableDispatchScope
import dev.redukt.test.assertions.ActionsAssertScope
import dev.redukt.test.tools.TestDispatchScope
import dev.redukt.thunk.CoThunkAction
import dev.redukt.thunk.ThunkAction

/**
 * The scope for a thunk under test.
 */
public interface ThunkTestScope<State> : MutableDispatchScope<State>, ActionsAssertScope {

    /**
     * Executes thunk function.
     */
    public fun testExecute()
}

/**
 * Creates [ThunkTestScope] for a [thunk] with [initialState], [initialClosure] and [initialOnDispatch].
 */
public fun <State> ThunkTestScope(
    thunk: ThunkAction<State>,
    initialState: State,
    initialClosure: DispatchClosure = EmptyDispatchClosure,
    initialOnDispatch: MutableDispatchScope<State>.(Action) -> Unit = { },
): ThunkTestScope<State> = ThunkTestScopeImpl(thunk, TestDispatchScope(initialState, initialClosure, initialOnDispatch))

/**
 * The scope for a suspending thunk under test.
 */
public interface CoThunkTestScope<State> : MutableDispatchScope<State>, ActionsAssertScope {

    /**
     * Executes thunk suspending function.
     */
    public suspend fun testExecute()
}

/**
 * Creates [ThunkTestScope] for a [thunk] with [initialState], [initialClosure] and [initialOnDispatch].
 */
public fun <State> CoThunkTestScope(
    thunk: CoThunkAction<State>,
    initialState: State,
    initialClosure: DispatchClosure = EmptyDispatchClosure,
    initialOnDispatch: MutableDispatchScope<State>.(Action) -> Unit = { },
): CoThunkTestScope<State> = CoThunkTestScopeImpl(thunk, TestDispatchScope(initialState, initialClosure, initialOnDispatch))

private class ThunkTestScopeImpl<State>(
    private val thunk: ThunkAction<State>,
    private val scope: TestDispatchScope<State>,
) : ThunkTestScope<State>, MutableDispatchScope<State> by scope, ActionsAssertScope by scope {
    override fun testExecute() {
        thunk.run { scope.execute() }
    }
}

private class CoThunkTestScopeImpl<State>(
    private val thunk: CoThunkAction<State>,
    private val scope: TestDispatchScope<State>,
) : CoThunkTestScope<State>, MutableDispatchScope<State> by scope, ActionsAssertScope by scope {
    override suspend fun testExecute() {
        thunk.run { scope.execute() }
    }
}