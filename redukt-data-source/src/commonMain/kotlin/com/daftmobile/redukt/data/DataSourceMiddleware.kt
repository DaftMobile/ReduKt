package com.daftmobile.redukt.data

import com.daftmobile.redukt.core.middleware.Middleware
import com.daftmobile.redukt.core.middleware.consumingDispatch

public fun <State> dataSourceMiddleware(): Middleware<State> = {
    val resolver = dataSourceResolver
    consumingDispatch<DataSourceCall<Any?, Any?>> { action ->
        dispatch(DataSourceAction(action.key, DataSourcePayload.Started(action.request)))
        runCatching { resolver.resolve(action.key).get(action.request) }
            .onSuccess { dispatch(DataSourceAction(action.key, DataSourcePayload.Success(action.request, it))) }
            .onFailure { dispatch(DataSourceAction(action.key, DataSourcePayload.Failure(action.request, it))) }
    }
}
