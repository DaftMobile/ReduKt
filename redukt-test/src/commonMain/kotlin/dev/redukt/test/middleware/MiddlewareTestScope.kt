package dev.redukt.test.middleware

import dev.redukt.core.*
import dev.redukt.core.closure.*
import dev.redukt.core.middleware.MiddlewareScope
import dev.redukt.core.coroutines.DispatchCoroutineScope
import dev.redukt.core.coroutines.ForegroundJobAction
import dev.redukt.core.coroutines.SingleForegroundJobRegistry
import dev.redukt.core.middleware.Middleware
import dev.redukt.test.assertions.ActionsAssertScope
import dev.redukt.test.tools.ImmutableLocalClosure
import dev.redukt.test.tools.MockForegroundJobRegistry
import dev.redukt.test.tools.Queue
import dev.redukt.test.tools.SpyingDispatchScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope

public interface MiddlewareTestScope<State> : ActionsAssertScope {

    public var state: State

    public var closure: DispatchClosure

    public fun testAction(action: Action)

    public suspend fun testJobAction(action: ForegroundJobAction)

    public fun testJobActionIn(scope: CoroutineScope, action: Action): Job

    public fun assertNext(block: ActionsAssertScope.() -> Unit)
}

public fun MiddlewareTestScope<*>.testAllActions(vararg actions: Action): Unit = actions.forEach { testAction(it) }

@PublishedApi
internal class DefaultMiddlewareTestScope<State>(
    middleware: Middleware<State>,
    initialState: State,
    initialClosure: DispatchClosure = EmptyDispatchClosure,
) : MiddlewareTestScope<State> {

    override var state: State = initialState
    override var closure: DispatchClosure =
        ImmutableLocalClosure(::closure) + MockForegroundJobRegistry() + initialClosure

    private val dispatchSpy = SpyingDispatchScope(::state, ::closure)
    private val middlewareSpy = SpyingMiddlewareScope(dispatchSpy)
    private val middlewareDispatch = middleware(middlewareSpy)

    override fun testAction(action: Action) = middlewareDispatch(action)

    override suspend fun testJobAction(action: ForegroundJobAction) = coroutineScope<Unit> {
        testJobActionIn(this, action)
    }

    override fun testJobActionIn(scope: CoroutineScope, action: Action): Job {
        val registry = SingleForegroundJobRegistry()
        closure += registry + DispatchCoroutineScope(scope)
        middlewareDispatch(action)
        return registry.consume()
    }

    override val unverified get() = dispatchSpy.unverified

    override val history get() = dispatchSpy.history

    override fun assertNext(block: ActionsAssertScope.() -> Unit) = middlewareSpy.block()
}

private class SpyingMiddlewareScope<State>(
    private val dispatchScope: DispatchScope<State>
) : MiddlewareScope<State>, ActionsAssertScope, DispatchScope<State> by dispatchScope {

    private val nextSpy = SpyingDispatchScope(dispatchScope::currentState, dispatchScope::closure)
    override fun next(action: Action) = nextSpy.dispatch(action)
    override val history: List<Action> get() = nextSpy.history
    override val unverified: Queue<Action> get() = nextSpy.unverified
}
