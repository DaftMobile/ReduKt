package com.github.lupuuss.redukt.core.middleware

import com.github.lupuuss.redukt.core.Action
import com.github.lupuuss.redukt.core.scope.DispatchScope

fun interface Middleware<State> {

    fun DispatchScope<State>.process(action: Action): Status

    enum class Status {
        Consumed, Passed
    }
}

internal fun <State> Middleware<State>.processWith(scope: DispatchScope<State>, action: Action) = scope.process(action)