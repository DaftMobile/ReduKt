package com.daftmobile.redukt.test.thunk

import com.daftmobile.redukt.core.closure.DispatchClosure
import com.daftmobile.redukt.core.closure.EmptyDispatchClosure
import com.daftmobile.redukt.test.assertions.assertNoMoreActions
import com.daftmobile.redukt.thunk.CoThunk
import com.daftmobile.redukt.thunk.Thunk

/**
 * Creates a test environment for a [thunk] with [initialState] and [initialClosure].
 * If [strict] is true, every [test] call must process all [ThunkTestScope.unverified] actions.
 */
public class ThunkTester<State>(
    public val thunk: Thunk<State>,
    public val initialState: State,
    public val initialClosure: DispatchClosure = EmptyDispatchClosure,
    public val strict: Boolean = true,
) {

    /**
     * Runs a thunk test from a [block].
     *
     * @param strict if it is not null, overwrites [strict] param value for this test call.
     */
    public inline fun test(strict: Boolean = this.strict, block: ThunkTestScope<State>.() -> Unit) {
        ThunkTestScope(thunk, initialState, initialClosure).apply {
            block()
            if (strict) assertNoMoreActions()
        }
    }
}

/**
 * Creates a [ThunkTester] for [this] thunk with [initialState], [initialClosure] and [strict].
 */
public fun <State> Thunk<State>.tester(
    initialState: State,
    initialClosure: DispatchClosure = EmptyDispatchClosure,
    strict: Boolean = true,
): ThunkTester<State> = ThunkTester(this, initialState, initialClosure, strict)

/**
 * Creates a test environment for a [thunk] with [initialState] and [initialClosure].
 * If [strict] is true, every [test] call must process all [CoThunkTestScope.unverified] actions.
 */
public class CoThunkTester<State>(
    public val thunk: CoThunk<State>,
    public val initialState: State,
    public val initialClosure: DispatchClosure = EmptyDispatchClosure,
    public val strict: Boolean = true,
) {

    /**
     * Runs a thunk test from a [block].
     *
     * @param strict if it is not null, overwrites [strict] param value for this test call.
     */
    public inline fun test(strict: Boolean = this.strict, block: CoThunkTestScope<State>.() -> Unit) {
        CoThunkTestScope(thunk, initialState, initialClosure).apply {
            block()
            if (strict) assertNoMoreActions()
        }
    }
}
/**
 * Creates a [CoThunkTester] for [this] thunk with [initialState], [initialClosure] and [strict].
 */
public fun <State> CoThunk<State>.tester(
    initialState: State,
    initialClosure: DispatchClosure = EmptyDispatchClosure,
    strict: Boolean = true,
): CoThunkTester<State> = CoThunkTester(this, initialState, initialClosure, strict)
