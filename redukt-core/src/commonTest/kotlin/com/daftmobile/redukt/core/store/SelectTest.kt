package com.daftmobile.redukt.core.store

import app.cash.turbine.test
import com.daftmobile.redukt.core.KnownAction
import com.daftmobile.redukt.core.MockSelector
import com.daftmobile.redukt.core.closure.EmptyDispatchClosure
import com.daftmobile.redukt.core.store.select.select
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SelectTest {

    private val store = Store(
        initialState = 1,
        reducer = { state, action ->
            when (action) {
                is KnownAction.A -> state + 1
                else -> state
            }
        },
        middlewares = emptyList(),
        initialClosure = EmptyDispatchClosure
    )
    @Test
    fun shouldProperlyReturnInitialSubState() {
        store.select { it.toString() }.value shouldBe "1"
    }

    @Test
    fun shouldProperlyReturnChangedValue() {
        val subState = store.select { it.toString() }
        store.dispatch(KnownAction.A)
        subState.value shouldBe "2"
        store.dispatch(KnownAction.A)
        subState.value shouldBe "3"
    }

    @Test
    fun shouldReturnStateFlowThatProvidesMappedStateUpdates() = runTest {
        val subState = store.select { it.toString() }
        subState.test {
            awaitItem() shouldBe "1"
            store.dispatch(KnownAction.A)
            awaitItem() shouldBe "2"
            store.dispatch(KnownAction.A)
            awaitItem() shouldBe "3"
        }
    }

    @Test
    fun shouldReturnStateFlowThatProvidesMappedStateUpdatesForMultipleSubscribers() = runTest {
        val subState = store.select { it.coerceAtMost(3).toString() }
        val results = mutableListOf<List<String>>()
        repeat(10) {
            launch {
                results.add(subState.take(3).toList())
            }
        }
        yield()
        store.dispatch(KnownAction.A)
        yield()
        store.dispatch(KnownAction.A)
        yield()
        store.dispatch(KnownAction.A)
        yield()
        results shouldHaveSize 10
        results.forEach {
            it shouldBe listOf("1", "2", "3")
        }
    }

    @Test
    fun shouldEmitValuesProperlyOnRepeatedCollect() = runTest {
        val subState = store.select { it.toString() }
        subState.test {
            store.dispatch(KnownAction.A)
            awaitItem() shouldBe "1"
            awaitItem() shouldBe "2"
        }
        subState.test {
            awaitItem() shouldBe "2"
            store.dispatch(KnownAction.A)
            awaitItem() shouldBe "3"
        }
    }

    @Test
    fun shouldNotEmitSelectedStateIfNotChanged() = runTest {
        val subState = store.select { "3" }
        subState.test {
            awaitItem() shouldBe "3"
            store.dispatch(KnownAction.A)
            expectNoEvents()
        }
    }

    @Test
    fun shouldNotRecalculatedSelectedStateIfStateNotChanged() = runTest {
        val selector = MockSelector<Int, Int> { it * it }
        val subState = store.select(selector = selector::call)
        subState.test {
            awaitItem() shouldBe 1
            store.dispatch(KnownAction.B)
            selector.callsCounter shouldBe 1
        }
    }

    @Test
    fun shouldNotCallSelectorIfNotAccessed() = runTest {
        val selector = MockSelector<Int, Int> { it * it }
        store.select(selector = selector::call)
        selector.callsCounter shouldBe 0
    }

    @Test
    fun shouldProperlyDeliverInitialNull() = runTest {
        val subState = store.select { it.takeIf { it > 2 } }
        subState.test {
            awaitItem() shouldBe null
        }
    }

    @Test
    fun shouldNotRepeatNull() = runTest {
        val subState = store.select { it.takeIf { it > 2 } }
        subState.test {
            skipItems(1)
            store.dispatch(KnownAction.A)
            expectNoEvents()
        }
    }

    @Test
    fun shouldProperlyHandleNullTransition() = runTest {
        val subState = store.select { it.takeIf { it > 2 } }
        subState.test {
            skipItems(1)
            store.dispatch(KnownAction.A)
            store.dispatch(KnownAction.A)
            awaitItem() shouldBe 3
        }
    }

    @Test
    fun shouldProperlyDeliverNullOnRepeatedCollect() = runTest {
        val subState = store.select { it.takeIf { it > 2 } }
        subState.test { skipItems(1) }
        subState.test {
            awaitItem() shouldBe null
        }
    }
}