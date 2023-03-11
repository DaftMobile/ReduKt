package com.daftmobile.redukt.core.store.select

import io.kotest.matchers.collections.beMonotonicallyIncreasing
import io.kotest.matchers.collections.shouldContainDuplicates
import io.kotest.matchers.should
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
class SelectStateFlowCollectStressTest {

    private val stateFlow = MutableStateFlow(0)
    private val selection = SelectStateFlow(
        flow = stateFlow,
        selector = Selector { it }
    )

    @Test
    fun stressTestFastUpdates() = runTest {
        newFixedThreadPoolContext(10, "TestPool").use { pool ->
            val collectors = List(9) {
                async(pool) {
                    selection.take(1_000).toList()
                }
            }
            val emitter = launch(pool) {
                while (isActive) stateFlow.update { it + 1 }
            }
            val results = collectors.awaitAll()
            results.onEach(::println).forEach { it should beMonotonicallyIncreasing() }
            emitter.cancel()
        }
    }

    @Test
    fun stressTestSlowUpdates() = runTest {
        newFixedThreadPoolContext(10, "TestPool").use { pool ->
            val collectorsCount = 20
            val syncChannel = Channel<Int>()
            val collectors = List(collectorsCount) {
                launch(pool) {
                    selection.take(10).collect { syncChannel.send(it) }
                }
            }
            val emitter = launch(pool) {
                while (isActive) {
                    stateFlow.update { it + 1 }
                    List(collectorsCount) { syncChannel.receive() }.shouldContainDuplicates()
                }
            }
            collectors.joinAll()
            emitter.cancel()
        }
    }


}
