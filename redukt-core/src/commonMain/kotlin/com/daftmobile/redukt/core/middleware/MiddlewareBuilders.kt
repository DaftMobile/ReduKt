package com.daftmobile.redukt.core.middleware

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.DispatchFunction
import com.daftmobile.redukt.core.DispatchScope

/**
 * Creates a middleware.
 *
 * Example of usage:
 * ```
 * fun customMiddleware() = middleware<AppState> { action ->
 *     when (action) {
 *        is CustomAction -> dispatch(OtherAction)
 *        else -> next(action)
 *     }
 * }
 * ```
 */
public inline fun <State> middleware(
    crossinline block: MiddlewareScope<State>.(action: Action) -> Unit
): Middleware<State> = { { block(it); } }

/**
 * Creates a middleware with [consumingDispatch] that consumes actions with given supertype [T].
 * Other actions are passed to the next middleware.
 *
 *  Example of usage:
 * ```
 * sealed interface CustomAction : Action {
 *    object A : CustomAction
 *    object B : CustomAction
 * }
 *
 * fun customMiddleware() = consumingMiddleware<AppState, CustomAction> { action ->
 *     when (action) { // this when is exhaustive!
 *        CustomAction.A -> TODO()
 *        CustomAction.B -> TODO()
 *     }
 * }
 * ```
 */
public inline fun <State, reified T : Action> consumingMiddleware(
    crossinline block: DispatchScope<State>.(T) -> Unit
): Middleware<State> = { consumingDispatch<T> { block(it) } }

/**
 * Creates a middleware with [translucentDispatch] that executes a given [block] and passes every action to the next middleware.
 *
 *  Example of usage:
 * ```
 * fun debugMiddleware() = translucentMiddleware<AppState> { action -> println(action) }
 * ```
 */
public inline fun <State> translucentMiddleware(
    crossinline block: DispatchScope<State>.(Action) -> Unit
): Middleware<State> = { translucentDispatch { block(it) } }

/**
 * This function only returns given [dispatch]. It has a few benefits over simple [DispatchFunction] lambda that are illustrated with this example:
 * ```
 * fun counterMiddleware1(): Middleware<AppState> = {
 *    var i = restorePreviousValue() // comma is required here to compile
 *    { action -> // label must be defined manually here
 *       if (action is ResetCounter) {
 *          i = 0
 *          return next(action) // early exit requires a label to lambda
 *       }
 *       i++
 *       next(action)
 *    }
 * }
 *
 * fun counterMiddleware2(): Middleware<AppState> = {
 *    var i = restorePreviousValue() // comma is NOT required here to compile
 *    dispatchFunction { action ->
 *       if (action is ResetCounter) {
 *          i = 0
 *          return@dispatchFunction next(action) // default label can be referred here
 *       }
 *       i++
 *       next(action)
 *    }
 * }
 * ```
 */
public fun MiddlewareScope<*>.dispatchFunction(dispatch: DispatchFunction): DispatchFunction = dispatch

/**
 * Creates a dispatch function that consumes actions with given supertype [T].
 * Other actions are passed to the next middleware.
 *
 * Example of usage:
 * ```
 * sealed interface CustomAction : Action {
 *    object A : CustomAction
 *    object B : CustomAction
 * }
 *
 * fun customMiddleware(): Middleware<AppState> = {
 *    consumingDispatch<CustomAction> { action ->
 *        when (action) { // this when is exhaustive!
 *           CustomAction.A -> TODO()
 *           CustomAction.B -> TODO()
 *        }
 *    }
 * }
 * ```
 * @see consumingMiddleware
 */
public inline fun <reified T : Action> MiddlewareScope<*>.consumingDispatch(
    crossinline block: (T) -> Unit
): DispatchFunction = {
    if (it is T) block(it)
    else next(it)
}

/**
 * Creates a dispatch function that executes a given [block] and passes every action to the next middleware.
 *
 * Example of usage:
 * ```
 * fun debugMiddleware(): Middleware<AppState> = {
 *    translucentDispatch { action ->
 *       println(action)
 *    }
 * }
 * ```
 * @see translucentMiddleware
 */
public inline fun MiddlewareScope<*>.translucentDispatch(crossinline block: DispatchFunction): DispatchFunction = {
    block(it)
    next(it)
}

/**
 * Creates a dispatch function that executes a given [block] for every action with given supertype [T] and passes every action to the next middleware.
 *
 * Example of usage:
 * ```
 * inline fun <reified T : Action> filteredDebugMiddleware(): Middleware<AppState> = {
 *    translucentDispatchOf<T> { action ->
 *       println(action)
 *    }
 * }
 * ```
 * @see translucentDispatch
 */
public inline fun <reified T : Action> MiddlewareScope<*>.translucentDispatchOf(
    crossinline block: (T) -> Unit
): DispatchFunction = {
    if (it is T) block(it)
    next(it)
}