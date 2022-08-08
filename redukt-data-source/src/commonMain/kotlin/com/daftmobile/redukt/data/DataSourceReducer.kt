package com.daftmobile.redukt.data

import com.daftmobile.redukt.core.Reducer

public typealias PayloadReducer<State, Payload> = (state: State, payload: Payload) -> State

public inline fun <Request, Response, State> createDataSourceReducer(
    key: DataSourceKey<Request, Response>,
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

@PublishedApi
internal inline fun <reified T> Any?.cast(): T = this as T
