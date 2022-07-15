package com.daftmobile.redukt.test.middleware

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.context.DispatchContext
import com.daftmobile.redukt.core.context.EmptyDispatchContext
import com.daftmobile.redukt.core.middleware.Middleware
import com.daftmobile.redukt.core.middleware.MiddlewareStatus
import com.daftmobile.redukt.test.assertions.ActionsAssertScope
import com.daftmobile.redukt.test.tools.SpyingDispatchScope

public interface MiddlewareTestScope<State> : ActionsAssertScope {

    public var state: State

    public var dispatchContext: DispatchContext

    public suspend fun onAction(action: Action): MiddlewareStatus

    public suspend fun onAllActions(vararg actions: Action): List<MiddlewareStatus>
}

internal class DefaultMiddlewareTestScope<State>(
    private val middleware: Middleware<State>,
    initialState: State,
    initialContext: DispatchContext = EmptyDispatchContext,
): MiddlewareTestScope<State> {

    override var state: State = initialState
    override var dispatchContext: DispatchContext = initialContext

    private val dispatchScope = SpyingDispatchScope( ::state, ::dispatchContext)

    override suspend fun onAction(action: Action) = middleware(dispatchScope, action)

    override suspend fun onAllActions(vararg actions: Action): List<MiddlewareStatus> = actions.map { onAction(it) }

    override val pipeline get() = dispatchScope.pipeline

    override val history get() = dispatchScope.history
}