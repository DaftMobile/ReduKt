package co.redukt.core

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class ReducerTest {

    @Test
    fun combineReducerShouldCallEachReducerWithProperOrderAndArguments() {
        val reducerMultiply: Reducer<Int> = { state, action ->
            when (action) {
                is KnownAction.A -> state * 2
                else -> state
            }
        }
        val reducerAdd: Reducer<Int> = { state, action ->
            when (action) {
                is KnownAction.A -> state + 2
                else -> state
            }
        }
        combineReducers(reducerMultiply, reducerAdd)(2, KnownAction.A) shouldBe 6
    }
}