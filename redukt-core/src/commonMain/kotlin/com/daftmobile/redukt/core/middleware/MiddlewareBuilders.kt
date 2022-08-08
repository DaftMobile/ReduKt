package com.daftmobile.redukt.core.middleware

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.Middleware
import com.daftmobile.redukt.core.scope.DispatchScope

public inline fun <State> middleware(
    crossinline block: suspend MiddlewareScope<State>.(action: Action) -> Unit
): Middleware<State> = { { block(it) } }

public inline fun <State, reified T : Action> consumingMiddleware(
    crossinline block: suspend DispatchScope<State>.(T) -> Unit
): Middleware<State> = {
    { action ->
        if (action !is T) next(action) else block(action)
    }
}

public inline fun <State> translucentMiddleware(
    crossinline block: suspend DispatchScope<State>.(Action) -> Unit
): Middleware<State> = {
    { action ->
        block(action)
        next(action)
    }
}