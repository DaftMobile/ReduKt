package com.daftmobile.redukt.core.store

import app.cash.turbine.test
import com.daftmobile.redukt.core.KnownAction
import com.daftmobile.redukt.core.coroutines.DispatchCoroutineScope
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SelectTest {

    private val testScope = TestScope()
    private val store = Store(1, { _, _ -> 2 }, emptyList(), DispatchCoroutineScope(testScope))
    @Test
    fun shouldProperlyReturnInitialSubState() {
        store.select { it.toString() }.value shouldBe "1"
    }

    @Test
    fun shouldReturnStateFlowThatProvidesMappedStateUpdates() = runTest {
        val subState = store.select { it.toString() }
        subState.test {
            awaitItem() shouldBe "1"
            store.dispatch(KnownAction.A)
            testScope.advanceUntilIdle()
            awaitItem() shouldBe "2"
        }
    }
}