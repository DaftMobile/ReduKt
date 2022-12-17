package dev.redukt.test.middleware

import dev.redukt.core.closure.DispatchClosure
import dev.redukt.core.closure.EmptyDispatchClosure
import dev.redukt.core.middleware.Middleware
import dev.redukt.test.assertions.assertNoMoreActions

/**
 * Creates a test environment for a [middleware] with [initialState] and [initialClosure].
 * If [strict] is true, every [test] call must process all [MiddlewareTestScope.unverified] actions.
 */
public class MiddlewareTester<State>(
    public val middleware: Middleware<State>,
    public val initialState: State,
    public val initialClosure: DispatchClosure = EmptyDispatchClosure,
    public val strict: Boolean
) {

    /**
     * Runs a middleware test from a [block].
     *
     * @param overwriteStrict if it is not null, overwrites [strict] param value for this test call.
     */
    public inline fun test(overwriteStrict: Boolean? = null, block: MiddlewareTestScope<State>.() -> Unit) {
        MiddlewareTestScope(middleware, initialState, initialClosure).apply {
            block()
            if (overwriteStrict ?: strict) assertNoMoreActions()
        }
    }
}
