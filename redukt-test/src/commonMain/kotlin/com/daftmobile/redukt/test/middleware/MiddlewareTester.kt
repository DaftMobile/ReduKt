package com.daftmobile.redukt.test.middleware

import com.daftmobile.redukt.core.context.DispatchContext
import com.daftmobile.redukt.core.context.EmptyDispatchContext
import com.daftmobile.redukt.core.middleware.Middleware

class MiddlewareTester<State>(
    private val middleware: Middleware<State>,
    private val initialState: State,
    private val initialContext: DispatchContext = EmptyDispatchContext,
) {

    suspend fun test(block: suspend MiddlewareTestScope<State>.() -> Unit) {
        block(DefaultMiddlewareTestScope(middleware, initialState, initialContext))
    }

    fun runTest(block: suspend MiddlewareTestScope<State>.() -> Unit) {
        kotlinx.coroutines.test.runTest { test(block) }
    }
}