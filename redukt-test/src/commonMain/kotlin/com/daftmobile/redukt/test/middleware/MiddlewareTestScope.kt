package com.daftmobile.redukt.test.middleware

import com.daftmobile.redukt.core.*
import com.daftmobile.redukt.core.closure.*
import com.daftmobile.redukt.core.middleware.MiddlewareScope
import com.daftmobile.redukt.core.coroutines.DispatchCoroutineScope
import com.daftmobile.redukt.core.coroutines.SingleForegroundJobRegistry
import com.daftmobile.redukt.core.middleware.Middleware
import com.daftmobile.redukt.test.assertions.ActionsAssertScope
import com.daftmobile.redukt.test.tools.ImmutableLocalClosure
import com.daftmobile.redukt.test.tools.MockForegroundJobRegistry
import com.daftmobile.redukt.test.tools.Queue
import com.daftmobile.redukt.test.tools.SpyingDispatchScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope

public interface MiddlewareTestScope<State> : ActionsAssertScope {

    public var state: State

    public var closure: DispatchClosure

    public fun testAction(action: Action)

    public suspend fun testJobAction(action: JobAction)

    public fun testJobActionIn(scope: CoroutineScope, action: Action): Job

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
    override var closure: DispatchClosure =
        ImmutableLocalClosure(::closure) + MockForegroundJobRegistry() + initialClosure

    private val dispatchSpy = SpyingDispatchScope(::state, ::closure)
    private val middlewareSpy = SpyingMiddlewareScope(dispatchSpy)

    override fun testAction(action: Action) = middleware(middlewareSpy)(action)

    override suspend fun testJobAction(action: JobAction) = coroutineScope<Unit> {
        testJobActionIn(this, action)
    }

    override fun testJobActionIn(scope: CoroutineScope, action: Action): Job {
        val registry = SingleForegroundJobRegistry()
        closure += registry + DispatchCoroutineScope(scope)
        middleware(middlewareSpy)(action)
        return registry.consume()
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
