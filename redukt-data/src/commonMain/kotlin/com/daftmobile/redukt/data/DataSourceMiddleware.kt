package com.daftmobile.redukt.data

import com.daftmobile.redukt.core.coroutines.launchForeground
import com.daftmobile.redukt.core.middleware.Middleware
import com.daftmobile.redukt.core.middleware.consumingDispatch

/**
 * Consumes [DataSourceCall] and launches a [DataSource] call in a foreground coroutine.
 *
 * It requires [DataSourceResolver] in a dispatch closure that provides [DataSource]s. If [DataSource] with
 * [DataSourceCall.key] cannot be resolved it throws an exception with no dispatched actions.
 *
 * The foreground coroutine dispatches:
 * * [DataSourceAction] with [DataSourcePayload.Started] before a foreground coroutine launch.
 * * [DataSourceAction] with [DataSourcePayload.Success] after successful [DataSource] call.
 * * [DataSourceAction] with [DataSourcePayload.Failure] after failure [DataSource] call.
 * * [DataSourceAction] with [DataSourcePayload.Failure] with CancellationException after cancellation.
 *
 */
public val dataSourceMiddleware: Middleware<*> = {
    val resolver = dataSourceResolver
    consumingDispatch<DataSourceCall<Any?, Any?>> { action ->
        val dataSource = resolver.resolve(action.key) ?: throw MissingDataSourceException(action.key)
        dispatch(action.key.startAction(action.request))
        launchForeground {
            runCatching { dataSource.call(action.request) }
                .onSuccess { dispatch(action.key.successAction(action.request, it)) }
                .onFailure { dispatch(action.key.failureAction(action.request, it)) }
        }
    }
}
