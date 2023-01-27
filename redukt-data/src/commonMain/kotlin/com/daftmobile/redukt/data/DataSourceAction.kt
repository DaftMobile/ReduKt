package com.daftmobile.redukt.data

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.coroutines.ForegroundJobAction

/**
 * An action that triggers a [DataSource] (identified by a given [key]) call with a given [request] in a foreground coroutine.
 */
public data class DataSourceCall<Request, Response>(
    val key: PureDataSourceKey<DataSource<Request, Response>>,
    val request: Request
) : ForegroundJobAction

/**
 * An action associated with a [DataSource] identified by a given [key].
 * It represents an event that is described by a given [payload].
 */
public data class DataSourceAction<Request, Response>(
    val key: PureDataSourceKey<DataSource<Request, Response>>,
    val payload: DataSourcePayload<Request, Response>,
) : Action

/**
 * A payload for a [DataSourceAction].
 *
 * Table below describes, when certain payload should be used.
 *
 * | Payload                   	| Event                              	|
 * |---------------------------	|------------------------------------	|
 * | [DataSourcePayload.Started] 	| Before [DataSource] is called        	|
 * | [DataSourcePayload.Success] 	| After successful [DataSource] call   	|
 * | [DataSourcePayload.Failure] 	| After unsuccessful [DataSource] call 	|
 */
public sealed interface DataSourcePayload<Request, Response> {

    public val request: Request

    public data class Started<Request, Response>(override val request: Request) : DataSourcePayload<Request, Response>

    public data class Success<Request, Response>(
        override val request: Request,
        val response: Response,
    ) : DataSourcePayload<Request, Response>

    public data class Failure<Request, Response>(
        override val request: Request,
        val error: Throwable
    ) : DataSourcePayload<Request, Response>
}

/**
 * Special type of payload that contains a [kotlin.Result]. It's not associated with [DataSourceAction] and it's only a util.
 */
public data class DataSourceResultPayload<Request, Response>(
    public val request: Request,
    public val result: Result<Response>
)

public fun <Request, Response> PureDataSourceKey<DataSource<Request, Response>>.successAction(
    request: Request,
    response: Response
): DataSourceAction<Request, Response> {
    return DataSourceAction(this, DataSourcePayload.Success(request, response))
}

public fun <Request, Response> PureDataSourceKey<DataSource<Request, Response>>.startAction(
    request: Request
): DataSourceAction<Request, Response> {
    return DataSourceAction(this, DataSourcePayload.Started(request))
}

public fun <Request, Response> PureDataSourceKey<DataSource<Request, Response>>.failureAction(
    request: Request,
    error: Throwable,
): DataSourceAction<Request, Response> {
    return DataSourceAction(this, DataSourcePayload.Failure(request, error))
}
