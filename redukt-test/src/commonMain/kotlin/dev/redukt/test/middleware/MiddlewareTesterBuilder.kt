package dev.redukt.test.middleware

import dev.redukt.core.closure.DispatchClosure
import dev.redukt.core.closure.EmptyDispatchClosure
import dev.redukt.core.middleware.Middleware

/**
 * Creates a [MiddlewareTester] for [this] middleware with [initialState], [initialClosure] and [strict].
 */
public fun <State> Middleware<State>.tester(
    initialState: State,
    initialClosure: DispatchClosure = EmptyDispatchClosure,
    strict: Boolean = true,
): MiddlewareTester<State> = MiddlewareTester(this, initialState, initialClosure, strict)