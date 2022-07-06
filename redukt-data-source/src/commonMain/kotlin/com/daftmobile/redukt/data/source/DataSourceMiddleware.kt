package com.daftmobile.redukt.data.source

import com.daftmobile.redukt.core.middleware.consumingMiddleware
import com.daftmobile.redukt.core.middleware.middlewareClosure

fun <State> dataSourceMiddleware() = middlewareClosure {
    val dataSourceResolver = dispatchContext[DataSourcesConfig].resolver
    consumingMiddleware<State, DataSourceCall<Any?, Any?>> { action ->
        dispatch(DataSourceAction(action.key, DataSourcePayload.Started(action.request)))
        runCatching { dataSourceResolver.resolve(action.key).get(action.request) }
            .onSuccess { dispatch(DataSourceAction(action.key, DataSourcePayload.Success(action.request, it))) }
            .onFailure { dispatch(DataSourceAction(action.key, DataSourcePayload.Failure(action.request, it))) }
    }
}