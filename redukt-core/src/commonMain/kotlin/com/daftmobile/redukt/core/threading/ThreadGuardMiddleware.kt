package com.daftmobile.redukt.core.threading

import com.daftmobile.redukt.core.middleware.Middleware
import com.daftmobile.redukt.core.middleware.translucentDispatch


public fun <State> threadGuardMiddleware(): Middleware<State> = {
    val initialThread = KtThread.current()
    translucentDispatch {
        val currentThread = KtThread.current()
        if (currentThread != initialThread) error("Calling dispatch from $currentThread, but you used $initialThread to dispatch first action! You should stick to initial thread!")
    }
}