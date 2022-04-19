package com.daftmobile.redukt.core.middleware

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.scope.DispatchScope

inline fun <State, reified T : Action,> consumingMiddleware(
    crossinline block: DispatchScope<State>.(T) -> Unit
) = Middleware<State> {
    if (it !is T) pass() else {
        block(it)
        consume()
    }
}

inline fun <State> translucentMiddleware(
    crossinline block: DispatchScope<State>.(Action) -> Unit
) = Middleware<State> {
    block(it)
    pass()
}

fun <State> middlewareClosure(@BuilderInference block: DispatchScope<State>.() -> Middleware<State>): Middleware<State> {
    var currentMiddleware: Middleware<State>
    val initMiddleware = translucentMiddleware<State> {
        currentMiddleware = block()
        currentMiddleware.apply { process(it) }
    }
    currentMiddleware = initMiddleware
    return Middleware { currentMiddleware.run { process(it) } }
}