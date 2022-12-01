package co.redukt.test.middleware

import co.redukt.core.closure.DispatchClosure
import co.redukt.core.closure.EmptyDispatchClosure
import co.redukt.core.middleware.Middleware

public fun <State> Middleware<State>.tester(
    initialState: State,
    initialClosure: DispatchClosure = EmptyDispatchClosure
): MiddlewareTester<State> = MiddlewareTester(this, initialState, initialClosure)