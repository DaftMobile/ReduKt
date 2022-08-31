package com.daftmobile.redukt.core.closure

import com.daftmobile.redukt.core.TestDispatchClosure
import io.kotest.matchers.collections.shouldNotBeIn
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.test.Test

class CoreLocalClosureTest {

    @Test
    fun shouldDelegateToBaseClosureWhenLocalNotPresent() {
        val baseClosure = TestDispatchClosure()
        val closure = CoreLocalClosure { baseClosure }
        closure[TestDispatchClosure] shouldBe baseClosure
    }


    @Test
    fun shouldReturnLocalClosureWhenLocalPresent() {
        val baseClosure = TestDispatchClosure()
        val localClosure = TestDispatchClosure()
        val closure = CoreLocalClosure { baseClosure }
        closure.registerNewSlot(localClosure)
        closure[TestDispatchClosure] shouldBe localClosure
        closure[TestDispatchClosure] shouldNotBe baseClosure
    }

    @Test
    fun shouldReturnLatestLocalClosureWhenMultipleLocalsPresent() {
        val baseClosure = TestDispatchClosure()
        val firstLocalClosure = TestDispatchClosure()
        val secondLocalClosure = TestDispatchClosure()
        val closure = CoreLocalClosure { baseClosure }
        closure.registerNewSlot(firstLocalClosure)
        closure.registerNewSlot(secondLocalClosure)
        closure[TestDispatchClosure] shouldBe secondLocalClosure
        closure[TestDispatchClosure] shouldNotBeIn arrayOf(baseClosure, firstLocalClosure)
    }

    @Test
    fun shouldReturnNextAvailableLocalClosureWhenLocalSlotRemoved() {
        val baseClosure = TestDispatchClosure()
        val firstLocalClosure = TestDispatchClosure()
        val secondLocalClosure = TestDispatchClosure()
        val closure = CoreLocalClosure { baseClosure }
        closure.registerNewSlot(firstLocalClosure)
        closure.registerNewSlot(secondLocalClosure).also(closure::removeSlot)
        closure[TestDispatchClosure] shouldBe firstLocalClosure
        closure[TestDispatchClosure] shouldNotBeIn arrayOf(baseClosure, secondLocalClosure)
    }

    @Test
    fun shouldReturnBaseClosureWhenAllLocalSlotsRemoved() {
        val baseClosure = TestDispatchClosure()
        val firstLocalClosure = TestDispatchClosure()
        val closure = CoreLocalClosure { baseClosure }
        closure.registerNewSlot(firstLocalClosure).also(closure::removeSlot)
        closure[TestDispatchClosure] shouldBe baseClosure
        closure[TestDispatchClosure] shouldNotBe firstLocalClosure
    }
}