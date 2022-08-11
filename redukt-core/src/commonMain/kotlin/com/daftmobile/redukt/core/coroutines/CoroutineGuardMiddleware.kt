package com.daftmobile.redukt.core.coroutines

import com.daftmobile.redukt.core.middleware.Middleware
import com.daftmobile.redukt.core.middleware.middleware
import kotlinx.coroutines.withContext

public fun <State> coroutineGuardMiddleware(): Middleware<State> = middleware {
    withContext(dispatchContext[StoreCoroutineDispatcher].dispatcher) {
        next(it)
    }
}