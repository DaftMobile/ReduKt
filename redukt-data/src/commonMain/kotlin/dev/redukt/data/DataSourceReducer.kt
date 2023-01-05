package dev.redukt.data

import dev.redukt.core.Reducer

public typealias PayloadReducer<State, Payload> = (state: State, payload: Payload) -> State

/**
 * Creates a [Reducer] that:
 * * Calls [onStart] on [DataSourceAction] with [DataSourcePayload.Started] and a given [key].
 * * Calls [onSuccess] on [DataSourceAction] with [DataSourcePayload.Success] and a given [key].
 * * Calls [onFailure] on [DataSourceAction] with [DataSourcePayload.Failure] and a given [key].
 * * Calls [onElse] otherwise.
 *
 * By default, all branches return state unchanged.
 *
 * Example of usage:
 *
 * ```kotlin
 * val reducer: Reducer<AppState> = createDataSourceReducer(
 *    key = FooDataSource,
 *    onStart = { state, (request) -> TODO("Create new state here") },
 *    onSuccess = { state, (request, response) -> TODO("Create new state here") },
 *    onFailure = { state, (request, error) -> TODO("Create new state here") },
 *    onElse = { state, action -> TODO("Create new state here") },
 * )
 * ```
 */
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

/**
 * Creates a [Reducer] that:
 * * Calls [onStart] on [DataSourceAction] with [DataSourcePayload.Started] and a given [key].
 * * Calls [onResult] with [kotlin.Result] on [DataSourceAction] with [DataSourcePayload.Success] or [DataSourcePayload.Failure] and a given [key].
 * * Calls [onElse] otherwise.
 *
 * Example of usage:
 *
 * ```kotlin
 * val reducer: Reducer<AppState> = createDataSourceReducer(
 *    key = FooDataSource,
 *    onStart = { state, (request) -> TODO("Create new state here") },
 *    onResult = { state, (request, result) -> TODO("Create new state here") },
 *    onElse = { state, action -> TODO("Create new state here") },
 * )
 * ```
 */
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