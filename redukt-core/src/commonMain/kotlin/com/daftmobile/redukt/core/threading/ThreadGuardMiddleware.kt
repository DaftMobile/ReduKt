package com.daftmobile.redukt.core.threading

import com.daftmobile.redukt.core.middleware.Middleware
import com.daftmobile.redukt.core.middleware.middlewareClosure
import com.daftmobile.redukt.core.middleware.translucentMiddleware

public fun <State> threadGuardMiddleware(): Middleware<State> = middlewareClosure {
    val initialThread = KtThread.current()
    translucentMiddleware {
        val currentThread = KtThread.current()
        if (currentThread != initialThread) {
            error("Calling dispatch from $currentThread, but you used $initialThread to dispatch first action! You should stick to initial thread!")
        }
    }
}