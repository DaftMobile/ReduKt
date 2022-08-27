package com.daftmobile.redukt.core.middleware

import com.daftmobile.redukt.core.*

public inline fun <State> middleware(
    crossinline block: MiddlewareScope<State>.(action: Action) -> Unit
): Middleware<State> = { { block(it) } }

public inline fun <State, reified T : Action> consumingMiddleware(
    crossinline block: DispatchScope<State>.(T) -> Unit
): Middleware<State> = { consumingDispatch<T> { block(it) } }

public inline fun <State> translucentMiddleware(
    crossinline block: DispatchScope<State>.(Action) -> Unit
): Middleware<State> = { translucentDispatch { block(it) } }

public inline fun MiddlewareScope<*>.dispatchFunction(noinline dispatch: DispatchFunction): DispatchFunction = dispatch

public inline fun <reified T : Action> MiddlewareScope<*>.consumingDispatch(
    crossinline block: (T) -> Unit
): DispatchFunction = {
    if (it is T) block(it)
    else next(it)
}

public inline fun MiddlewareScope<*>.translucentDispatch(crossinline dispatch: DispatchFunction): DispatchFunction = {
    dispatch(it)
    next(it)
}