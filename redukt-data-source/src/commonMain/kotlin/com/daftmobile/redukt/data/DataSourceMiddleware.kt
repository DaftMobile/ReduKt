package com.daftmobile.redukt.data

import com.daftmobile.redukt.core.middleware.Middleware
import com.daftmobile.redukt.core.middleware.consumingMiddleware
import com.daftmobile.redukt.core.middleware.middlewareClosure

public fun <State> dataSourceMiddleware(): Middleware<State> = middlewareClosure {
    val resolver = dataSourceResolver
    consumingMiddleware<_, DataSourceCall<Any?, Any?>> { action ->
        dispatch(DataSourceAction(action.key, DataSourcePayload.Started(action.request)))
        runCatching { resolver.resolve(action.key).get(action.request) }
            .onSuccess { dispatch(DataSourceAction(action.key, DataSourcePayload.Success(action.request, it))) }
            .onFailure { dispatch(DataSourceAction(action.key, DataSourcePayload.Failure(action.request, it))) }
    }
}
