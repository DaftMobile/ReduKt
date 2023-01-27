package com.daftmobile.redukt.data

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.Reducer
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class CreateDataSourceReducerTest {

    private object IntToString : DataSourceKey<Int, String>

    private object NotHandledDataSource : DataSourceKey<Int, String>

    private object UnknownAction : Action

    private data class AppState(
        val request: Int? = null,
        val response: String? = null,
        val error: Throwable? = null
    )

    private val reducer: Reducer<AppState> = createDataSourceReducer(
        key = IntToString,
        onStart = { state, payload -> state.copy(request = payload.request) },
        onSuccess = { state, payload -> state.copy(request = payload.request, response = payload.response) },
        onFailure = { state, payload -> state.copy(request = payload.request, error = payload.error) },
        onElse = { _, _ -> AppState(request = -1) }
    )

    @Test
    fun shouldCreateReducerThatReturnsStateFromStartedOnStartedAction() {
        reducer(AppState(), DataSourceAction(IntToString, DataSourcePayload.Started(1)))
            .shouldBe(AppState(request = 1))
    }

    @Test
    fun shouldCreateReducerThatReturnsStateFromSuccessOnSuccessAction() {
        reducer(AppState(), DataSourceAction(IntToString, DataSourcePayload.Success(1, "1")))
            .shouldBe(AppState(request = 1, response = "1"))
    }

    @Test
    fun shouldCreateReducerThatReturnsStateFromFailureOnFailureAction() {
        val exception = Exception("Failed!")
        reducer(AppState(), DataSourceAction(IntToString, DataSourcePayload.Failure(1, exception)))
            .shouldBe(AppState(request = 1, error = exception))
    }

    @Test
    fun shouldCreateReducerThatReturnsStateFromElseWhenKeyDoesNotMatch() {
        reducer(AppState(), DataSourceAction(NotHandledDataSource, DataSourcePayload.Success(1, "1")))
            .shouldBe(AppState(request = -1))
    }

    @Test
    fun shouldCreateReducerThatReturnsStateFromElseWhenUnknownAction() {
        reducer(AppState(), UnknownAction).shouldBe(AppState(request = -1))
    }
}