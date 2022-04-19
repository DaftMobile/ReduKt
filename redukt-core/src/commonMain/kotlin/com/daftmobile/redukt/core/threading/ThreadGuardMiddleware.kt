package com.daftmobile.redukt.core.threading

import com.daftmobile.redukt.core.middleware.middlewareClosure
import com.daftmobile.redukt.core.middleware.translucentMiddleware

fun <State> threadGuardMiddleware() = middlewareClosure<State> {
    val initialThread = Thread.current()
    translucentMiddleware {
        val currentThread = Thread.current()
        if (currentThread != initialThread) {
            error("Calling dispatch from $currentThread, but you used $initialThread to dispatch first action! You should stick to initial thread!")
        }
    }
}