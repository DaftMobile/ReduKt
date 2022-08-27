package com.daftmobile.redukt.test.middleware

import com.daftmobile.redukt.core.*
import com.daftmobile.redukt.core.closure.DispatchClosure
import com.daftmobile.redukt.core.closure.EmptyDispatchClosure
import com.daftmobile.redukt.core.closure.LocalClosure
import com.daftmobile.redukt.core.closure.withLocalClosure
import com.daftmobile.redukt.core.middleware.MiddlewareScope
import com.daftmobile.redukt.core.coroutines.DispatchCoroutineScope
import com.daftmobile.redukt.core.coroutines.EmptyForegroundJobRegistry
import com.daftmobile.redukt.core.middleware.Middleware
import com.daftmobile.redukt.test.assertions.ActionsAssertScope
import com.daftmobile.redukt.test.tools.Queue
import com.daftmobile.redukt.test.tools.SpyingDispatchScope
import kotlinx.coroutines.coroutineScope

public interface MiddlewareTestScope<State> : ActionsAssertScope {

    public var state: State

    public var closure: DispatchClosure

    public fun testAction(action: Action)

    public suspend fun awaitTestAction(action: JobAction)

    public fun testNext(block: ActionsAssertScope.() -> Unit)
}

public fun MiddlewareTestScope<*>.testAllActions(vararg actions: Action): Unit = actions.forEach { testAction(it) }

@PublishedApi
internal class DefaultMiddlewareTestScope<State>(
    private val middleware: Middleware<State>,
    initialState: State,
    initialClosure: DispatchClosure = EmptyDispatchClosure,
) : MiddlewareTestScope<State> {

    override var state: State = initialState
    override var closure: DispatchClosure = LocalClosure { EmptyForegroundJobRegistry() } + initialClosure

    private val dispatchSpy = SpyingDispatchScope(::state, ::closure)
    private val middlewareSpy = SpyingMiddlewareScope(dispatchSpy)

    override fun testAction(action: Action) = middleware(middlewareSpy)(action)

    override suspend fun awaitTestAction(action: JobAction) = coroutineScope {
        middlewareSpy.withLocalClosure(DispatchCoroutineScope(this)) {
            middleware(middlewareSpy)(action)
        }
    }

    override val pipeline get() = dispatchSpy.pipeline

    override val history get() = dispatchSpy.history

    override fun testNext(block: ActionsAssertScope.() -> Unit) = middlewareSpy.block()
}

private class SpyingMiddlewareScope<State>(
    private val dispatchScope: DispatchScope<State>
) : MiddlewareScope<State>, ActionsAssertScope, DispatchScope<State> by dispatchScope {

    private val nextSpy = SpyingDispatchScope(dispatchScope::currentState, dispatchScope::closure)
    override fun next(action: Action) = nextSpy.dispatch(action)
    override val history: List<Action> get() = nextSpy.history
    override val pipeline: Queue<Action> get() = nextSpy.pipeline
}
