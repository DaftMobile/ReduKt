package com.daftmobile.redukt.test.middleware

import com.daftmobile.redukt.core.closure.DispatchClosure
import com.daftmobile.redukt.core.closure.EmptyDispatchClosure
import com.daftmobile.redukt.core.middleware.Middleware
import kotlinx.coroutines.ExperimentalCoroutinesApi

public class MiddlewareTester<State>(
    private val middleware: Middleware<State>,
    private val initialState: State,
    private val initialClosure: DispatchClosure = EmptyDispatchClosure,
) {
    public fun test(block: MiddlewareTestScope<State>.() -> Unit) {
        block(DefaultMiddlewareTestScope(middleware, initialState, initialClosure))
    }

    @ExperimentalCoroutinesApi
    public fun suspendTest(block: suspend MiddlewareTestScope<State>.() -> Unit) {
        kotlinx.coroutines.test.runTest {
            block(DefaultMiddlewareTestScope(middleware, initialState, initialClosure))
        }
    }
}