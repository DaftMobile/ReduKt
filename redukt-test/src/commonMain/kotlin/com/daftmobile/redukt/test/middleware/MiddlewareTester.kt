package com.daftmobile.redukt.test.middleware

import com.daftmobile.redukt.core.closure.DispatchClosure
import com.daftmobile.redukt.core.closure.EmptyDispatchClosure
import com.daftmobile.redukt.core.middleware.Middleware
import com.daftmobile.redukt.test.assertions.assertNoMoreActions

/**
 * Creates a test environment for a [middleware] with [initialState] and [initialClosure].
 * If [strict] is true, every [test] call must process all [MiddlewareTestScope.unverified] actions.
 */
public class MiddlewareTester<State>(
    public val middleware: Middleware<State>,
    public val initialState: State,
    public val initialClosure: DispatchClosure = EmptyDispatchClosure,
    public val strict: Boolean = true
) {

    /**
     * Runs a middleware test from a [block]. Each test call initiates middleware separately.
     *
     * @param strict if it is not null, overwrites [strict] param value for this test call.
     * @param state provides initial state for this test
     * @param closure provides initial closure for this test
     */
    public inline fun test(
        strict: Boolean? = null,
        state: State = initialState,
        closure: DispatchClosure = EmptyDispatchClosure,
        block: MiddlewareTestScope<State>.() -> Unit
    ) {
        MiddlewareTestScope(middleware, state, initialClosure + closure).apply {
            block()
            if (strict ?: this@MiddlewareTester.strict) assertNoMoreActions()
        }
    }
}


/**
 * Creates a [MiddlewareTester] for [this] middleware with [initialState], [initialClosure] and [strict].
 */
public fun <State> Middleware<State>.tester(
    initialState: State,
    initialClosure: DispatchClosure = EmptyDispatchClosure,
    strict: Boolean = true,
): MiddlewareTester<State> = MiddlewareTester(this, initialState, initialClosure, strict)
