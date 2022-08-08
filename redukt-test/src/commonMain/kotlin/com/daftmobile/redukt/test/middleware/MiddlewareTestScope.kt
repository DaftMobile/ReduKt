package com.daftmobile.redukt.test.middleware

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.Middleware
import com.daftmobile.redukt.core.context.DispatchContext
import com.daftmobile.redukt.core.context.EmptyDispatchContext
import com.daftmobile.redukt.core.middleware.MiddlewareScope
import com.daftmobile.redukt.core.scope.DispatchScope
import com.daftmobile.redukt.test.assertions.ActionsAssertScope
import com.daftmobile.redukt.test.tools.Queue
import com.daftmobile.redukt.test.tools.SpyingDispatchScope

public interface MiddlewareTestScope<State> : ActionsAssertScope {

    public var state: State

    public var dispatchContext: DispatchContext

    public suspend fun testAction(action: Action)

    public fun assertNext(block: ActionsAssertScope.() -> Unit)
}

public suspend fun MiddlewareTestScope<*>.testAllActions(vararg actions: Action): Unit = actions.forEach { testAction(it) }

internal class DefaultMiddlewareTestScope<State>(
    private val middleware: Middleware<State>,
    initialState: State,
    initialContext: DispatchContext = EmptyDispatchContext,
): MiddlewareTestScope<State> {

    override var state: State = initialState
    override var dispatchContext: DispatchContext = initialContext

    private val dispatchSpy = SpyingDispatchScope(::state, ::dispatchContext)
    private val middlewareSpy = SpyingMiddlewareScope(dispatchSpy)

    override suspend fun testAction(action: Action) = middleware(middlewareSpy)(action)

    override val pipeline get() = dispatchSpy.pipeline

    override val history get() = dispatchSpy.history

    override fun assertNext(block: ActionsAssertScope.() -> Unit) = middlewareSpy.block()
}

private class SpyingMiddlewareScope<State>(
    private val dispatchScope: DispatchScope<State>
    ): MiddlewareScope<State>, ActionsAssertScope, DispatchScope<State> by dispatchScope {

    private val nextSpy = SpyingDispatchScope(dispatchScope::state, dispatchScope::dispatchContext)
    override suspend fun next(action: Action) = nextSpy.dispatch(action)
    override val history: List<Action> get() = nextSpy.history
    override val pipeline: Queue<Action> get() = nextSpy.pipeline
}