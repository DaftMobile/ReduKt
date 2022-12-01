package co.redukt.test.middleware

import co.redukt.core.closure.DispatchClosure
import co.redukt.core.closure.EmptyDispatchClosure
import co.redukt.core.middleware.Middleware

public class MiddlewareTester<State>(
    public val middleware: Middleware<State>,
    public val initialState: State,
    public val initialClosure: DispatchClosure = EmptyDispatchClosure,
) {

    public inline fun test(block: MiddlewareTestScope<State>.() -> Unit) {
        block(DefaultMiddlewareTestScope(middleware, initialState, initialClosure))
    }
}
