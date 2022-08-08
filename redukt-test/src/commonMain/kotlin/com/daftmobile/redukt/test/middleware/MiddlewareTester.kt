package com.daftmobile.redukt.test.middleware

import com.daftmobile.redukt.core.Middleware
import com.daftmobile.redukt.core.context.DispatchContext
import com.daftmobile.redukt.core.context.EmptyDispatchContext
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
public class MiddlewareTester<State>(
    private val middleware: Middleware<State>,
    private val initialState: State,
    private val initialContext: DispatchContext = EmptyDispatchContext,
) {

    public suspend fun test(block: suspend MiddlewareTestScope<State>.() -> Unit) {
        block(DefaultMiddlewareTestScope(middleware, initialState, initialContext))
    }

    public fun runTest(block: suspend MiddlewareTestScope<State>.() -> Unit) {
        kotlinx.coroutines.test.runTest { test(block) }
    }
}