package com.daftmobile.redukt.core.store

import app.cash.turbine.test
import com.daftmobile.redukt.core.KnownAction
import com.daftmobile.redukt.core.MockSelector
import com.daftmobile.redukt.core.closure.EmptyDispatchClosure
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

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
        assertEquals("2", subState.value)
        store.dispatch(KnownAction.A)
        assertEquals("3", subState.value)
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
            assertEquals(1, selector.callsCounter)
        }
    }

    @Test
    fun shouldNotCallSelectorIfNotAccessed() = runTest {
        val selector = MockSelector<Int, Int> { it * it }
        store.select(selector = selector::call)
        assertEquals(0, selector.callsCounter)
    }
}