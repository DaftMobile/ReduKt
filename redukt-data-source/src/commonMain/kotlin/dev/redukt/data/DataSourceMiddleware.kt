package dev.redukt.data

import dev.redukt.core.coroutines.launchForeground
import dev.redukt.core.middleware.Middleware
import dev.redukt.core.middleware.consumingDispatch

public val dataSourceMiddleware: Middleware<*> = {
    val resolver = dataSourceResolver
    consumingDispatch<DataSourceCall<Any?, Any?>> { action ->
        dispatch(DataSourceAction(action.key, DataSourcePayload.Started(action.request)))
        launchForeground {
            runCatching { resolver.call(action.key, action.request) }
                .onSuccess { dispatch(DataSourceAction(action.key, DataSourcePayload.Success(action.request, it))) }
                .onFailure { dispatch(DataSourceAction(action.key, DataSourcePayload.Failure(action.request, it))) }
        }
    }
}