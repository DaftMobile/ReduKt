package com.github.lupuuss.redukt.core

import com.github.lupuuss.redukt.core.scope.DispatchScope

internal interface TestActionConsumer {
    fun consume(dispatchScope: DispatchScope<*>, action: Action)
}