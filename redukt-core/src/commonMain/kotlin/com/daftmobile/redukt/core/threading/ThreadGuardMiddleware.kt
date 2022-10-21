package com.daftmobile.redukt.core.threading

import com.daftmobile.redukt.core.middleware.Middleware
import com.daftmobile.redukt.core.middleware.translucentDispatch

/**
 * Creates a middleware that ensures single-threaded usage of the dispatch.
 * Actions should be dispatched with thread that created this middleware (main thread preferably).
 */
public fun <State> threadGuardMiddleware(): Middleware<State> = {
    val initialThread = KtThread.current()
    translucentDispatch {
        val currentThread = KtThread.current()
        if (currentThread != initialThread) error(
            "Calling dispatch from $currentThread, but you used $initialThread to create a store! You should stick to initial thread!"
        )
    }
}
