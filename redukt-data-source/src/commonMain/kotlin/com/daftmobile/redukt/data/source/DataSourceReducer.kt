package com.daftmobile.redukt.data.source

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.Reducer

@Suppress("UNCHECKED_CAST")
public fun <Request, Response, State> dataSourceReducer(
    key: DataSourceKey<Request, Response>,
    state: State,
    action: Action,
    reducerLogic: SingleDataSourceReducerScope<Request, Response, State>.() -> Unit
): State {
    val scope = SingleDataSourceReducerScopeImpl<Request, Response, State>()
    scope.apply(reducerLogic)
    if (action !is DataSourceAction<*, *> || action.key != key) return scope.elseReducer(state, action)
    return when (val payload = action.payload) {
        is DataSourcePayload.Started<*, *> -> scope.startReducer(state, payload.cast())
        is DataSourcePayload.Success<*, *> -> scope.successReducer(state, payload.cast())
        is DataSourcePayload.Failure<*, *> -> scope.failureReducer(state, payload.cast())
    }
}

@Suppress("UNCHECKED_CAST")
public fun <State> dataSourceReducer(
    state: State,
    action: Action,
    reducerLogic: MultiDataSourceReducerScope<State>.() -> Unit
): State {
    val scope = MultiDataSourceReducerScopeImpl<State>()
    scope.apply(reducerLogic)
    if (action !is DataSourceAction<*, *>) return scope.elseReducer(state, action)
    return when (val payload = action.payload) {
        is DataSourcePayload.Started<*, *> -> scope.startReducers[action.key]?.invoke(state, payload)
            ?: scope.elseReducer(state, action)
        is DataSourcePayload.Success<*, *> -> scope.successReducers[action.key]?.invoke(state, payload)
            ?: scope.elseReducer(state, action)
        is DataSourcePayload.Failure<*, *> -> scope.failureReducers[action.key]?.invoke(state, payload)
            ?: scope.elseReducer(state, action)
    }
}

public typealias TypedReducer<State, Action> = (state: State, action: Action) -> State

@DslMarker
public annotation class DataSourceReducerDslMarker

public interface SingleDataSourceReducerScope<Request, Response, State> {

    @DataSourceReducerDslMarker
    public fun onStart(reducer: TypedReducer<State, DataSourcePayload.Started<Request, Response>>)

    @DataSourceReducerDslMarker
    public fun onSuccess(reducer: TypedReducer<State, DataSourcePayload.Success<Request, Response>>)

    @DataSourceReducerDslMarker
    public fun onFailure(reducer: TypedReducer<State, DataSourcePayload.Failure<Request, Response>>)

    @DataSourceReducerDslMarker
    public fun onElse(reducer: Reducer<State>)
}

public interface MultiDataSourceReducerScope<State> {
    @DataSourceReducerDslMarker
    public infix fun <Request, Response> DataSourceKey<Request, Response>.onStart(
        reducer: TypedReducer<State, DataSourcePayload.Started<Request, Response>>
    )

    @DataSourceReducerDslMarker
    public infix fun <Request, Response> DataSourceKey<Request, Response>.onSuccess(
        reducer: TypedReducer<State, DataSourcePayload.Success<Request, Response>>
    )

    @DataSourceReducerDslMarker
    public infix fun <Request, Response> DataSourceKey<Request, Response>.onFailure(
        reducer: TypedReducer<State, DataSourcePayload.Failure<Request, Response>>
    )

    @DataSourceReducerDslMarker
    public fun onElse(reducer: Reducer<State>)
}

internal class SingleDataSourceReducerScopeImpl<Request, Response, State> :
    SingleDataSourceReducerScope<Request, Response, State> {

    var startReducer: TypedReducer<State, DataSourcePayload.Started<Request, Response>> = { state, _ -> state }
    var successReducer: TypedReducer<State, DataSourcePayload.Success<Request, Response>> = { state, _ -> state }
    var failureReducer: TypedReducer<State, DataSourcePayload.Failure<Request, Response>> = { state, _ -> state }
    var elseReducer: Reducer<State> = { state, _ -> state }

    override fun onStart(reducer: TypedReducer<State, DataSourcePayload.Started<Request, Response>>) {
        startReducer = reducer
    }

    override fun onSuccess(reducer: TypedReducer<State, DataSourcePayload.Success<Request, Response>>) {
        successReducer = reducer
    }

    override fun onFailure(reducer: TypedReducer<State, DataSourcePayload.Failure<Request, Response>>) {
        failureReducer = reducer
    }

    override fun onElse(reducer: Reducer<State>) {
        elseReducer = reducer
    }

}

internal class MultiDataSourceReducerScopeImpl<State> : MultiDataSourceReducerScope<State> {

    val startReducers = mutableMapOf<DataSourceKey<*, *>, TypedReducer<State, DataSourcePayload.Started<*, *>>>()
    val successReducers = mutableMapOf<DataSourceKey<*, *>, TypedReducer<State, DataSourcePayload.Success<*, *>>>()
    val failureReducers = mutableMapOf<DataSourceKey<*, *>, TypedReducer<State, DataSourcePayload.Failure<*, *>>>()
    var elseReducer: Reducer<State> = { state, _ -> state }

    override fun <Request, Response> DataSourceKey<Request, Response>.onStart(
        reducer: TypedReducer<State, DataSourcePayload.Started<Request, Response>>
    ) {
        startReducers[this] = reducer.cast()
    }

    override fun <Request, Response> DataSourceKey<Request, Response>.onSuccess(
        reducer: TypedReducer<State, DataSourcePayload.Success<Request, Response>>
    ) {
        successReducers[this] = reducer.cast()
    }

    override fun <Request, Response> DataSourceKey<Request, Response>.onFailure(
        reducer: TypedReducer<State, DataSourcePayload.Failure<Request, Response>>
    ) {
        failureReducers[this] = reducer.cast()
    }

    override fun onElse(reducer: Reducer<State>) {
        elseReducer = reducer
    }

}

private inline fun <reified T> Any?.cast(): T = this as T