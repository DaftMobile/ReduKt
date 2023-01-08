package dev.redukt.data

import dev.redukt.core.coroutines.launchForeground
import dev.redukt.core.middleware.Middleware
import dev.redukt.core.middleware.consumingDispatch
import dev.redukt.data.resolver.MissingDataSourceException
import dev.redukt.data.resolver.dataSourceResolver

/**
 * Consumes [DataSourceCall] and launches a [DataSource] call in a foreground coroutine.
 *
 * It requires [dev.redukt.data.resolver.DataSourceResolver] in a dispatch closure that provides [DataSource]s.
 * If [DataSource] with [DataSourceCall.key] cannot be resolved it throws an exception with no dispatched actions.
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
        dispatch(DataSourceAction(action.key, DataSourcePayload.Started(action.request)))
        launchForeground {
            runCatching { dataSource.call(action.request) }
                .onSuccess { dispatch(DataSourceAction(action.key, DataSourcePayload.Success(action.request, it))) }
                .onFailure { dispatch(DataSourceAction(action.key, DataSourcePayload.Failure(action.request, it))) }
        }
    }
}
