package com.daftmobile.redukt.test.middleware

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.context.DispatchContext
import com.daftmobile.redukt.core.context.EmptyDispatchContext
import com.daftmobile.redukt.core.middleware.Middleware
import com.daftmobile.redukt.test.assertions.expectNoMoreActions
import com.daftmobile.redukt.test.tools.SpyingDispatchScope

class MiddlewareTester<State>(
    private val middleware: Middleware<State>,
    private val initialState: State,
    private val initialContext: DispatchContext = EmptyDispatchContext,
) {

    fun test(block: MiddlewareTesterScope<State>.() -> Unit) {
        block(DefaultMiddlewareTesterScope(middleware, initialState, initialContext))
    }

    fun testStrict(block: MiddlewareTesterScope<State>.() -> Unit) {
        DefaultMiddlewareTesterScope(middleware, initialState, initialContext).apply {
            block()
            expectNoMoreActions()
        }
    }
}

class DefaultMiddlewareTesterScope<State>(
    private val middleware: Middleware<State>,
    initialState: State,
    initialContext: DispatchContext = EmptyDispatchContext,
): MiddlewareTesterScope<State> {

    override var state: State = initialState
    override var dispatchContext: DispatchContext = initialContext

    private val dispatchScope = SpyingDispatchScope( ::state, ::dispatchContext)

    override fun onAction(action: Action) = middleware.run { dispatchScope.process(action) }

    override fun onAllActions(vararg actions: Action): List<Middleware.Status> = actions.map(::onAction)

    override val pipeline get() = dispatchScope.pipeline

    override val history get() = dispatchScope.history
}