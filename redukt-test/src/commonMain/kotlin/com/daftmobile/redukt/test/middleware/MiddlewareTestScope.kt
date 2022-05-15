package com.daftmobile.redukt.test.middleware

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.context.DispatchContext
import com.daftmobile.redukt.core.context.EmptyDispatchContext
import com.daftmobile.redukt.core.middleware.Middleware
import com.daftmobile.redukt.test.assertions.ActionsAssertScope
import com.daftmobile.redukt.test.tools.SpyingDispatchScope

interface MiddlewareTestScope<State> : ActionsAssertScope {

    var state: State

    var dispatchContext: DispatchContext

    fun onAction(action: Action): Middleware.Status

    fun onAllActions(vararg actions: Action): List<Middleware.Status>
}

internal class DefaultMiddlewareTestScope<State>(
    private val middleware: Middleware<State>,
    initialState: State,
    initialContext: DispatchContext = EmptyDispatchContext,
): MiddlewareTestScope<State> {

    override var state: State = initialState
    override var dispatchContext: DispatchContext = initialContext

    private val dispatchScope = SpyingDispatchScope( ::state, ::dispatchContext)

    override fun onAction(action: Action) = middleware.run { dispatchScope.process(action) }

    override fun onAllActions(vararg actions: Action): List<Middleware.Status> = actions.map(::onAction)

    override val pipeline get() = dispatchScope.pipeline

    override val history get() = dispatchScope.history
}