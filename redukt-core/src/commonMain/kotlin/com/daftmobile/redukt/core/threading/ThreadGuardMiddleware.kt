package com.daftmobile.redukt.core.threading

import com.daftmobile.redukt.core.Middleware
import com.daftmobile.redukt.core.middleware.dispatchFunction


public fun <State> threadGuardMiddleware(): Middleware<State> = {
    val initialThread = KtThread.current()
    dispatchFunction {
        val currentThread = KtThread.current()
        if (currentThread != initialThread) {
            error("Calling dispatch from $currentThread, but you used $initialThread to dispatch first action! You should stick to initial thread!")
        }
    }
}