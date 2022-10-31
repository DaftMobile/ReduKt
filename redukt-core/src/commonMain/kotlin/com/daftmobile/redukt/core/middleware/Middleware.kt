package com.daftmobile.redukt.core.middleware

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.DispatchFunction
import com.daftmobile.redukt.core.DispatchScope

/**
 * Type alias for a [Redux middleware](https://redux.js.org/tutorials/fundamentals/part-4-store#middleware) equivalent.
 * The key difference is that *dispatch*, *getState* and *next* are provided by a [MiddlewareScope].
 *
 * Example of defining a middleware:
 * ```
 * fun customMiddleware(): Middleware<AppState> = {
 *    { action ->
 *       when (action) {
 *          is CustomAction -> dispatch(OtherAction)
 *          else -> next(action)
 *       }
 *    }
 * }
 * ```
 * @see [middleware], [consumingMiddleware], [translucentMiddleware].
 */
public typealias Middleware<State> = MiddlewareScope<State>.() -> DispatchFunction

/**
 * The scope that provides necessary operations for a middleware.
 */
public interface MiddlewareScope<out State> : DispatchScope<State> {

    /**
     * Passes [action] to the next middleware in the pipeline.
     */
    public fun next(action: Action)
}

@PublishedApi
internal class MergedMiddlewareScope<State>(
    dispatchScope: DispatchScope<State>,
    private val nextFunction: DispatchFunction
): MiddlewareScope<State>, DispatchScope<State> by dispatchScope {

    override fun next(action: Action) = nextFunction(action)
}
