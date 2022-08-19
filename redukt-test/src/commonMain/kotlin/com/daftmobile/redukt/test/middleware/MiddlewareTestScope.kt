package com.daftmobile.redukt.test.middleware

import com.daftmobile.redukt.core.*
import com.daftmobile.redukt.core.closure.DispatchClosure
import com.daftmobile.redukt.core.closure.EmptyDispatchClosure
import com.daftmobile.redukt.core.middleware.MiddlewareScope
import com.daftmobile.redukt.core.coroutines.StoreCoroutineScope
import com.daftmobile.redukt.core.coroutines.EmptyForegroundJobRegistry
import com.daftmobile.redukt.core.middleware.Middleware
import com.daftmobile.redukt.test.assertions.ActionsAssertScope
import com.daftmobile.redukt.test.tools.Queue
import com.daftmobile.redukt.test.tools.SpyingDispatchScope
import kotlinx.coroutines.coroutineScope

public interface MiddlewareTestScope<State> : ActionsAssertScope {

    public var state: State

    public var dispatchClosure: DispatchClosure

    public fun testAction(action: Action)

    public suspend fun awaitTestAction(action: SuspendAction)

    public fun assertNext(block: ActionsAssertScope.() -> Unit)
}

public fun MiddlewareTestScope<*>.testAllActions(vararg actions: Action): Unit = actions.forEach { testAction(it) }

internal class DefaultMiddlewareTestScope<State>(
    private val middleware: Middleware<State>,
    initialState: State,
    initialClosure: DispatchClosure = EmptyDispatchClosure,
) : MiddlewareTestScope<State> {

    override var state: State = initialState
    override var dispatchClosure: DispatchClosure = EmptyForegroundJobRegistry() + initialClosure

    private val dispatchSpy = SpyingDispatchScope(::state, ::dispatchClosure)
    private val middlewareSpy = SpyingMiddlewareScope(dispatchSpy)

    override fun testAction(action: Action) = middleware(middlewareSpy)(dispatchClosure.asLocalScope(), action)

    override suspend fun awaitTestAction(action: SuspendAction) = coroutineScope {
        val closure = dispatchClosure + StoreCoroutineScope(this)
        middleware(middlewareSpy)(closure.asLocalScope(), action)
    }

    override val pipeline get() = dispatchSpy.pipeline

    override val history get() = dispatchSpy.history

    override fun assertNext(block: ActionsAssertScope.() -> Unit) = middlewareSpy.block()
}

private inline fun DispatchClosure.asLocalScope(): LocalClosureScope = LocalClosureScopeImpl(this)

private class LocalClosureScopeImpl(@DelicateReduKtApi override val closure: DispatchClosure) : LocalClosureScope


private class SpyingMiddlewareScope<State>(
    private val dispatchScope: DispatchScope<State>
) : MiddlewareScope<State>, ActionsAssertScope, DispatchScope<State> by dispatchScope {

    private val nextSpy = SpyingDispatchScope(dispatchScope::currentState, dispatchScope::closure)
    override fun next(action: Action) = nextSpy.dispatch(action)

    @DelicateReduKtApi
    override fun next(action: Action, closure: DispatchClosure) = nextSpy.dispatch(action, closure)

    override val history: List<Action> get() = nextSpy.history
    override val pipeline: Queue<Action> get() = nextSpy.pipeline
}