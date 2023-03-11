package com.daftmobile.redukt.core.store.select

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.updateAndGet
import org.jetbrains.kotlinx.lincheck.annotations.Operation
import org.jetbrains.kotlinx.lincheck.check
import org.jetbrains.kotlinx.lincheck.strategy.stress.StressOptions
import org.junit.Test

class SelectStateFlowLincheckStressTest {

    private val stateFlow = MutableStateFlow(0)
    private val selection = SelectStateFlow(
        flow = stateFlow,
        selector = Selector { it * 2 }
    )

    @Operation
    fun triggerUpdate() = stateFlow.updateAndGet { it + 1 }

    @Operation
    fun value() = selection.value

    @Operation
    suspend fun first() = selection.first()

    @Test
    fun stressTest() = StressOptions()
        .iterations(20)
        .invocationsPerIteration(2_000)
        .check(this::class)
}

