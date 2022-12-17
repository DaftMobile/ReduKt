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
import dev.redukt.test.tools.StubForegroundJobRegistry
import dev.redukt.test.tools.Queue
import dev.redukt.test.tools.MockDispatchScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope

/**
 * The scope for a single middleware under test.
 */
public interface MiddlewareTestScope<State> : ActionsAssertScope {

    /**
     * A state for a middleware under test.
     */
    public var state: State

    /**
     * A closure for a middleware under test.
     */
    public var closure: DispatchClosure

    /**
     * Calls middleware under test with given [action].
     */
    public fun testAction(action: Action)

    /**
     * Calls middleware under test with given [action] and joins foreground job.
     */
    public suspend fun testJobAction(action: ForegroundJobAction)

    /**
     * Calls middleware under test with given [action] and provides a [scope] for a foreground job.
     */
    public fun testJobActionIn(scope: CoroutineScope, action: Action): Job

    /**
     * Runs assertions block on actions passed to the next middleware by the middleware under test.
     */
    public fun verifyNext(block: ActionsAssertScope.() -> Unit)
}

/**
 * Calls [MiddlewareTestScope.testAction] for every action from [actions].
 */
public fun MiddlewareTestScope<*>.testAllActions(vararg actions: Action): Unit = actions.forEach { testAction(it) }

@PublishedApi
internal class DefaultMiddlewareTestScope<State>(
    middleware: Middleware<State>,
    initialState: State,
    initialClosure: DispatchClosure = EmptyDispatchClosure,
) : MiddlewareTestScope<State> {

    override var state: State = initialState
    override var closure: DispatchClosure =
        ImmutableLocalClosure(::closure) + StubForegroundJobRegistry() + initialClosure

    private val dispatchSpy = MockDispatchScope(::state, ::closure)
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

    override fun verifyNext(block: ActionsAssertScope.() -> Unit) = middlewareSpy.block()
}

private class SpyingMiddlewareScope<State>(
    private val dispatchScope: DispatchScope<State>
) : MiddlewareScope<State>, ActionsAssertScope, DispatchScope<State> by dispatchScope {

    private val nextSpy = MockDispatchScope(dispatchScope::currentState, dispatchScope::closure)
    override fun next(action: Action) = nextSpy.dispatch(action)
    override val history: List<Action> get() = nextSpy.history
    override val unverified: Queue<Action> get() = nextSpy.unverified
}
