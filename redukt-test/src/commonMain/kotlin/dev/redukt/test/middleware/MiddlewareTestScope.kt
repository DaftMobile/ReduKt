package dev.redukt.test.middleware

import dev.redukt.core.Action
import dev.redukt.core.DispatchScope
import dev.redukt.core.closure.DispatchClosure
import dev.redukt.core.closure.EmptyDispatchClosure
import dev.redukt.core.middleware.Middleware
import dev.redukt.core.middleware.MiddlewareScope
import dev.redukt.test.MutableDispatchScope
import dev.redukt.test.assertions.ActionsAssertScope
import dev.redukt.test.tools.Queue
import dev.redukt.test.tools.TestDispatchScope

/**
 * The scope for a middleware under test.
 */
public interface MiddlewareTestScope<State> : MutableDispatchScope<State>, ActionsAssertScope {

    /**
     * Calls middleware under test with given [action].
     */
    public fun testAction(action: Action)

    /**
     * Runs assertions block on actions passed to the next middleware by the middleware under test.
     */
    public fun verifyNext(block: ActionsAssertScope.() -> Unit)
}

/**
 * Calls [MiddlewareTestScope.testAction] for every action from [actions].
 */
public fun MiddlewareTestScope<*>.testAllActions(vararg actions: Action): Unit = actions.forEach { testAction(it) }

/**
 * Creates a [MiddlewareTestScope] for a [middleware] with [initialState], [initialClosure] and [initialOnDispatch].
 */
public fun <State> MiddlewareTestScope(
    middleware: Middleware<State>,
    initialState: State,
    initialClosure: DispatchClosure = EmptyDispatchClosure,
    initialOnDispatch: MutableDispatchScope<State>.(Action) -> Unit = { },
): MiddlewareTestScope<State> = MiddlewareTestScopeImpl(
    middleware = middleware,
    dispatchScope = TestDispatchScope(initialState, initialClosure, initialOnDispatch)
)

private class MiddlewareTestScopeImpl<State>(
    middleware: Middleware<State>,
    private val dispatchScope: TestDispatchScope<State>
) : MiddlewareTestScope<State>, ActionsAssertScope by dispatchScope, MutableDispatchScope<State> by dispatchScope {

    private val middlewareSpy = SpyingMiddlewareScope(dispatchScope)
    private val middlewareDispatch = middleware(middlewareSpy)

    override fun testAction(action: Action) = middlewareDispatch(action)

    override fun verifyNext(block: ActionsAssertScope.() -> Unit) = middlewareSpy.block()
}

private class SpyingMiddlewareScope<State>(
    private val dispatchScope: DispatchScope<State>
) : MiddlewareScope<State>, ActionsAssertScope, DispatchScope<State> by dispatchScope {

    private val nextSpy = TestDispatchScope(Unit)
    override fun next(action: Action) = nextSpy.dispatch(action)
    override val history: List<Action> get() = nextSpy.history
    override val unverified: Queue<Action> get() = nextSpy.unverified
    override fun clearActionsHistory() {
        nextSpy.clearActionsHistory()
    }
}
