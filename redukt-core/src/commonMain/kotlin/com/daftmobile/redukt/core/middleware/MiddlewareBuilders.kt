package com.daftmobile.redukt.core.middleware

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.scope.DispatchScope

inline fun <State, reified T : Action> consumingMiddleware(
    crossinline block: suspend DispatchScope<State>.(T) -> Unit
): Middleware<State> = { action ->
    if (action !is T) next(action) else {
        block(action)
        consume()
    }
}

inline fun <State> translucentMiddleware(
    crossinline block: suspend DispatchScope<State>.(Action) -> Unit
): Middleware<State> = { action ->
    block(action)
    next(action)
}

fun <State> middlewareClosure(@BuilderInference block: suspend DispatchScope<State>.() -> Middleware<State>): Middleware<State> {
    var currentMiddleware: Middleware<State>
    val initMiddleware = translucentMiddleware { action ->
        currentMiddleware = block()
        currentMiddleware(this, action)
    }
    currentMiddleware = initMiddleware
    return { action -> currentMiddleware(this, action) }
}