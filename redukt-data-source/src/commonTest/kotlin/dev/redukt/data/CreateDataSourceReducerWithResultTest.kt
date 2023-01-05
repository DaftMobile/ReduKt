package dev.redukt.data

import dev.redukt.core.Action
import dev.redukt.core.Reducer
import io.kotest.matchers.shouldBe
import kotlin.test.Test


internal class CreateDataSourceReducerWithResultTest {

    private object IntToString : DataSourceKey<Int, String>

    private object NotHandledDataSource : DataSourceKey<Int, String>

    private object UnknownAction : Action

    private data class AppState(
        val request: Int? = null,
        val result: Result<String>? = null,
    )

    private val reducer: Reducer<AppState> = createDataSourceReducer(
        key = IntToString,
        onStart = { state, payload -> state.copy(request = payload.request) },
        onResult = { state, payload -> state.copy(request = payload.request, result = payload.result) },
        onElse = { _, _ -> AppState(request = -1) }
    )

    @Test
    fun shouldCreateReducerThatReturnsStateFromStartedOnStartedAction() {
        reducer(AppState(), DataSourceAction(IntToString, DataSourcePayload.Started(1))) shouldBe AppState(request = 1)
    }

    @Test
    fun shouldCreateReducerThatReturnsStateFromResultOnSuccessAction() {
        reducer(AppState(), DataSourceAction(IntToString, DataSourcePayload.Success(1, "1")))
            .shouldBe(AppState(request = 1, result = Result.success("1")))
    }

    @Test
    fun shouldCreateReducerThatReturnsStateFromFailureOnFailureAction() {
        val exception = Exception("Failed!")
        reducer(AppState(), DataSourceAction(IntToString, DataSourcePayload.Failure(1, exception)))
            .shouldBe(AppState(request = 1, result = Result.failure(exception)))
    }

    @Test
    fun shouldCreateReducerThatReturnsStateFromElseWhenKeyDoesNotMatch() {
        reducer(AppState(), DataSourceAction(NotHandledDataSource, DataSourcePayload.Success(1, "1")))
            .shouldBe(AppState(request = -1))
    }

    @Test
    fun shouldCreateReducerThatReturnsStateFromElseWhenUnknownAction() {
        reducer(AppState(), UnknownAction) shouldBe AppState(request = -1)
    }
}