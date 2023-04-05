package com.daftmobile.redukt.test.thunk

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.closure.DispatchClosure
import com.daftmobile.redukt.core.closure.EmptyDispatchClosure
import com.daftmobile.redukt.test.MutableDispatchScope
import com.daftmobile.redukt.test.assertions.ActionsAssertScope
import com.daftmobile.redukt.test.tools.TestDispatchScope
import com.daftmobile.redukt.thunk.CoThunk
import com.daftmobile.redukt.thunk.Thunk

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
    thunk: Thunk<State>,
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
    thunk: CoThunk<State>,
    initialState: State,
    initialClosure: DispatchClosure = EmptyDispatchClosure,
    initialOnDispatch: MutableDispatchScope<State>.(Action) -> Unit = { },
): CoThunkTestScope<State> = CoThunkTestScopeImpl(
    thunk = thunk,
    scope = TestDispatchScope(initialState, initialClosure, initialOnDispatch)
)

private class ThunkTestScopeImpl<State>(
    private val thunk: Thunk<State>,
    private val scope: TestDispatchScope<State>,
) : ThunkTestScope<State>, MutableDispatchScope<State> by scope, ActionsAssertScope by scope {
    override fun testExecute() {
        thunk.run { scope.execute() }
    }
}

private class CoThunkTestScopeImpl<State>(
    private val thunk: CoThunk<State>,
    private val scope: TestDispatchScope<State>,
) : CoThunkTestScope<State>, MutableDispatchScope<State> by scope, ActionsAssertScope by scope {
    override suspend fun testExecute() {
        thunk.run { scope.execute() }
    }
}
