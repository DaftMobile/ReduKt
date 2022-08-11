package com.daftmobile.redukt.core.middleware

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.DispatchFunction
import com.daftmobile.redukt.core.DispatchScope

public inline fun <State> middleware(
    crossinline block: suspend MiddlewareScope<State>.(action: Action) -> Unit
): Middleware<State> = { { block(it) } }

public inline fun MiddlewareScope<*>.dispatchFunction(noinline dispatch: DispatchFunction): DispatchFunction = dispatch

public inline fun <reified T : Action> MiddlewareScope<*>.consumingDispatch(
    crossinline block: suspend (T) -> Unit
): DispatchFunction = {
    if (it is T) block(it)
    else next(it)
}

public inline fun MiddlewareScope<*>.translucentDispatch(crossinline block: DispatchFunction): DispatchFunction = {
    block(it)
    next(it)
}

public inline fun <State, reified T : Action> consumingMiddleware(
    crossinline block: suspend DispatchScope<State>.(T) -> Unit
): Middleware<State> = { consumingDispatch<T> { block(it) } }

public inline fun <State> translucentMiddleware(
    crossinline block: suspend DispatchScope<State>.(Action) -> Unit
): Middleware<State> = { translucentDispatch { block(it) } }