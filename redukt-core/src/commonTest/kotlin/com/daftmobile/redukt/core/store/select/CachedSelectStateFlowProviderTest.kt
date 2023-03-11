package com.daftmobile.redukt.core.store.select

import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.job
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class CachedSelectStateFlowProviderTest {
    private val testScope = TestScope()
    private val provider = CachedSelectStateFlowProvider(
        unusedFlowTimeout = 1.seconds,
        scope = testScope
    )
    private val baseFlow = MutableStateFlow(0)
    private val selector = Selector(selector = Int::toString)

    @Test
    fun shouldReturnTheSameInstanceBeforeTimeout() {
        val selection = provider.provide(baseFlow, selector)
        testScope.advanceTimeBy(500)
        provider.provide(baseFlow, selector) shouldBeSameInstanceAs selection
    }

    @Test
    fun shouldReturnTheNewInstanceAfterTimeout() {
        val selection = provider.provide(baseFlow, selector)
        testScope.advanceTimeBy(1_001)
        provider.provide(baseFlow, selector) shouldNotBeSameInstanceAs selection
    }

    @Test
    fun shouldReturnTheSameInstanceAfterTimeoutWhenActiveSubscription() {
        val selection = provider.provide(baseFlow, selector)
        val job = selection.launchIn(testScope)
        testScope.advanceTimeBy(10_000)
        provider.provide(baseFlow, selector) shouldBeSameInstanceAs selection
        job.cancel()
    }

    @Test
    fun shouldReturnDifferentInstanceWhenAllSubscribersOutAndAfterTimeout() {
        val selection = provider.provide(baseFlow, selector)
        val job = selection.launchIn(testScope)
        testScope.advanceTimeBy(10_000)
        job.cancel()
        testScope.advanceTimeBy(1_001)
        provider.provide(baseFlow, selector) shouldNotBeSameInstanceAs selection
    }

    @Test
    fun shouldCancelTimeoutCoroutinesWhenNoSubscribers() {
        val selection = provider.provide(baseFlow, selector)
        testScope.coroutineContext.job.children.count() shouldBe 1
        val subscriptions = List(3) { selection.launchIn(testScope) }
        subscriptions.forEach { it.cancel() }
        testScope.advanceTimeBy(1_001)
        testScope.coroutineContext.job.children.count() shouldBe 0
    }
}
