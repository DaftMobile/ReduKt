package co.redukt.test.middleware

import co.redukt.core.*
import co.redukt.core.closure.*
import co.redukt.core.middleware.MiddlewareScope
import co.redukt.core.coroutines.DispatchCoroutineScope
import co.redukt.core.coroutines.ForegroundJobAction
import co.redukt.core.coroutines.SingleForegroundJobRegistry
import co.redukt.core.middleware.Middleware
import co.redukt.test.assertions.ActionsAssertScope
import co.redukt.test.tools.ImmutableLocalClosure
import co.redukt.test.tools.MockForegroundJobRegistry
import co.redukt.test.tools.Queue
import co.redukt.test.tools.SpyingDispatchScope
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

    override val pipeline get() = dispatchSpy.pipeline

    override val history get() = dispatchSpy.history

    override fun assertNext(block: ActionsAssertScope.() -> Unit) = middlewareSpy.block()
}

private class SpyingMiddlewareScope<State>(
    private val dispatchScope: DispatchScope<State>
) : MiddlewareScope<State>, ActionsAssertScope, DispatchScope<State> by dispatchScope {

    private val nextSpy = SpyingDispatchScope(dispatchScope::currentState, dispatchScope::closure)
    override fun next(action: Action) = nextSpy.dispatch(action)
    override val history: List<Action> get() = nextSpy.history
    override val pipeline: Queue<Action> get() = nextSpy.pipeline
}
