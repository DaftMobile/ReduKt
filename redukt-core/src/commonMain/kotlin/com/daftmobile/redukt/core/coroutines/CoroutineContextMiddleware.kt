package com.daftmobile.redukt.core.coroutines

import com.daftmobile.redukt.core.middleware.Middleware
import com.daftmobile.redukt.core.middleware.middleware
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

public fun <State> coroutineContextMiddleware(context: CoroutineContext = EmptyCoroutineContext): Middleware<State> = middleware {
    withContext(closure[StoreCoroutineDispatcher].dispatcher + context) {
        next(it)
    }
}