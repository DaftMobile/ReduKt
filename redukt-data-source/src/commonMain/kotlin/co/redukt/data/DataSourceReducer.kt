package co.redukt.data

import co.redukt.core.Reducer

public typealias PayloadReducer<State, Payload> = (state: State, payload: Payload) -> State

public inline fun <Request, Response, State> createDataSourceReducer(
    key: DataSourceKey<DataSource<Request, Response>>,
    crossinline onStart: PayloadReducer<State, DataSourcePayload.Started<Request, Response>> = { s, _ -> s },
    crossinline onSuccess: PayloadReducer<State, DataSourcePayload.Success<Request, Response>> = { s, _ -> s },
    crossinline onFailure: PayloadReducer<State, DataSourcePayload.Failure<Request, Response>> = { s, _ -> s },
    crossinline onElse: Reducer<State> = { s, _ -> s },
): Reducer<State> = reducer@{ state, action ->
    if (action !is DataSourceAction<*, *> || action.key != key) return@reducer onElse(state, action)
    return@reducer when (val payload = action.payload) {
        is DataSourcePayload.Started<*, *> -> onStart(state, payload.cast())
        is DataSourcePayload.Success<*, *> -> onSuccess(state, payload.cast())
        is DataSourcePayload.Failure<*, *> -> onFailure(state, payload.cast())
    }
}

public inline fun <Request, Response, State> createDataSourceReducer(
    key: DataSourceKey<DataSource<Request, Response>>,
    crossinline onStart: PayloadReducer<State, DataSourcePayload.Started<Request, Response>> = { s, _ -> s },
    crossinline onResult: PayloadReducer<State, DataSourceResultPayload<Request, Response>> = { s, _ -> s },
    crossinline onElse: Reducer<State> = { s, _ -> s },
): Reducer<State> = createDataSourceReducer(
    key = key,
    onStart = onStart,
    onSuccess = { state, payload ->
        onResult(state, DataSourceResultPayload(payload.request, Result.success(payload.response)))
    },
    onFailure = { state, payload ->
        onResult(state, DataSourceResultPayload(payload.request, Result.failure(payload.error)))
    },
    onElse = onElse
)
