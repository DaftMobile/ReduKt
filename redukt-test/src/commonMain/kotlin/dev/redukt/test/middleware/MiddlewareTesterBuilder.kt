package dev.redukt.test.middleware

import dev.redukt.core.closure.DispatchClosure
import dev.redukt.core.closure.EmptyDispatchClosure
import dev.redukt.core.middleware.Middleware

public fun <State> Middleware<State>.tester(
    initialState: State,
    initialClosure: DispatchClosure = EmptyDispatchClosure
): MiddlewareTester<State> = MiddlewareTester(this, initialState, initialClosure)