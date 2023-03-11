package com.daftmobile.redukt.core.store.select

import io.kotest.matchers.collections.shouldContainDuplicates
import io.kotest.matchers.collections.shouldNotContainDuplicates
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.time.Duration.Companion.seconds

@OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
class CachedSelectStateFlowProviderStressTest {

    private val testScope = TestScope()
    private val provider = CachedSelectStateFlowProvider(
        unusedFlowTimeout = 1.seconds,
        scope = testScope
    )
    private val baseFlow = MutableStateFlow(0)
    private val intToString = Selector(selector = Int::toString)
    private val intToDouble = Selector(selector = Int::toDouble)

    @Test
    fun stressTest() = runTest {
        newFixedThreadPoolContext(10, "TestPool").use { pool ->
            val intToStringResults = List(5) {
                async(pool) {
                    provider.provide(baseFlow, intToString)
                }
            }
            val intToDoubleResults = List(5) {
                async(pool) {
                    provider.provide(baseFlow, intToDouble)
                }
            }
            intToStringResults.awaitAll().shouldContainDuplicates()
            intToDoubleResults.awaitAll().shouldContainDuplicates()
            (intToDoubleResults + intToStringResults).shouldNotContainDuplicates()
        }
    }
}
