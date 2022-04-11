package com.daftmobile.redukt.core

import com.daftmobile.redukt.core.scope.DispatchScope

internal interface TestActionConsumer {
    fun consume(dispatchScope: DispatchScope<*>, action: Action)
}