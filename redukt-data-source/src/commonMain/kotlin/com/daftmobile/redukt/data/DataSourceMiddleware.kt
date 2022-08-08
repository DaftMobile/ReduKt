package com.daftmobile.redukt.data

import com.daftmobile.redukt.core.Middleware
import com.daftmobile.redukt.core.middleware.dispatchFunction

public fun <State> dataSourceMiddleware(): Middleware<State> = {
    val resolver = dataSourceResolver
    dispatchFunction { it ->
        if (it !is DataSourceAction<*, *>) return@dispatchFunction next(it)
        val action = it.cast<DataSourceCall<Any?, Any?>>()
        dispatch(DataSourceAction(action.key, DataSourcePayload.Started(action.request)))
        runCatching { resolver.resolve(action.key).get(action.request) }
            .onSuccess { dispatch(DataSourceAction(action.key, DataSourcePayload.Success(action.request, it))) }
            .onFailure { dispatch(DataSourceAction(action.key, DataSourcePayload.Failure(action.request, it))) }
    }
}
