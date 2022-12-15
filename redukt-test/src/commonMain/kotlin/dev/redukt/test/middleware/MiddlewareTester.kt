package dev.redukt.test.middleware

import dev.redukt.core.closure.DispatchClosure
import dev.redukt.core.closure.EmptyDispatchClosure
import dev.redukt.core.middleware.Middleware
import dev.redukt.test.assertions.expectNoMoreActions

public class MiddlewareTester<State>(
    public val middleware: Middleware<State>,
    public val initialState: State,
    public val initialClosure: DispatchClosure = EmptyDispatchClosure,
    public val strict: Boolean
) {

    public inline fun test(overwriteStrict: Boolean? = null, block: MiddlewareTestScope<State>.() -> Unit) {
        DefaultMiddlewareTestScope(middleware, initialState, initialClosure).apply {
            block()
            if (overwriteStrict ?: strict) expectNoMoreActions()
        }
    }
}
