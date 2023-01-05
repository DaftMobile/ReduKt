package dev.redukt.data

import dev.redukt.core.Reducer

public typealias PayloadReducer<State, Payload> = (state: State, payload: Payload) -> State

public inline fun <Request, Response, State> createDataSourceReducer(
    key: PureDataSourceKey<DataSource<Request, Response>>,
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

@Suppress("UNCHECKED_CAST")
public inline fun <Request, Response, State> createDataSourceReducer(
    key: PureDataSourceKey<DataSource<Request, Response>>,
    crossinline onStart: PayloadReducer<State, DataSourcePayload.Started<Request, Response>> = { s, _ -> s },
    noinline onResult: PayloadReducer<State, DataSourceResultPayload<Request, Response>> = { s, _ -> s },
    crossinline onElse: Reducer<State> = { s, _ -> s },
): Reducer<State> = reducer@{ state, action ->
    if (action !is DataSourceAction<*, *> || action.key != key) return@reducer onElse(state, action)
    return@reducer when (val payload = action.payload) {
        is DataSourcePayload.Started<*, *> -> onStart(state, payload.cast())
        is DataSourcePayload.Success<*, *> -> {
            onResult(state, DataSourceResultPayload(payload.request as Request, Result.success(payload.response as Response)))
        }
        is DataSourcePayload.Failure<*, *> -> {
            onResult(state, DataSourceResultPayload(payload.request as Request, Result.failure(payload.error)))
        }
    }
}