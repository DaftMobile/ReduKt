package com.daftmobile.redukt.core.closure

import com.daftmobile.redukt.core.dispatchScope
import com.daftmobile.redukt.core.TestDispatchClosure
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldNotBeIn
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.test.Test

class LocalClosureTest {

    @Test
    fun shouldDelegateToBaseClosureWhenLocalNotPresent() {
        val baseClosure = TestDispatchClosure()
        val closure = LocalClosure { baseClosure }
        closure[TestDispatchClosure] shouldBe baseClosure
    }

    @Test
    fun shouldReturnLocalClosureWhenLocalPresent() {
        val baseClosure = TestDispatchClosure()
        val localClosure = TestDispatchClosure()
        val closure = LocalClosure { baseClosure }
        closure.registerNewSlot(localClosure)
        closure[TestDispatchClosure] shouldBe localClosure
        closure[TestDispatchClosure] shouldNotBe baseClosure
    }

    @Test
    fun shouldReturnLatestLocalClosureWhenMultipleLocalsPresent() {
        val baseClosure = TestDispatchClosure()
        val firstLocalClosure = TestDispatchClosure()
        val secondLocalClosure = TestDispatchClosure()
        val closure = LocalClosure { baseClosure }
        closure.registerNewSlot(firstLocalClosure)
        closure.registerNewSlot(secondLocalClosure)
        closure[TestDispatchClosure] shouldBe secondLocalClosure
        closure[TestDispatchClosure] shouldNotBeIn arrayOf(baseClosure, firstLocalClosure)
    }

    @Test
    fun shouldAccumulateValuesFromPreviousLocalClosuresToLatestOne() {
        val baseClosure = TestDispatchClosure()
        val closureElementA =  ClosureElementA()
        val firstLocalClosure = TestDispatchClosure() + closureElementA
        val secondLocalClosure = TestDispatchClosure()
        val closure = LocalClosure { baseClosure }
        closure.registerNewSlot(firstLocalClosure)
        closure.registerNewSlot(secondLocalClosure)
        closure[ClosureElementA] shouldBe closureElementA
    }

    @Test
    fun shouldReturnNextAvailableLocalClosureWhenLocalSlotRemoved() {
        val baseClosure = TestDispatchClosure()
        val firstLocalClosure = TestDispatchClosure()
        val secondLocalClosure = TestDispatchClosure()
        val closure = LocalClosure { baseClosure }
        closure.registerNewSlot(firstLocalClosure)
        closure.registerNewSlot(secondLocalClosure).also(closure::removeSlot)
        closure[TestDispatchClosure] shouldBe firstLocalClosure
        closure[TestDispatchClosure] shouldNotBeIn arrayOf(baseClosure, secondLocalClosure)
    }

    @Test
    fun shouldReturnBaseClosureWhenAllLocalSlotsRemoved() {
        val baseClosure = TestDispatchClosure()
        val firstLocalClosure = TestDispatchClosure()
        val closure = LocalClosure { baseClosure }
        closure.registerNewSlot(firstLocalClosure).also(closure::removeSlot)
        closure[TestDispatchClosure] shouldBe baseClosure
        closure[TestDispatchClosure] shouldNotBe firstLocalClosure
    }

    @Test
    fun findShouldReturnLocalClosureItselfWhenCalledWithLocalClosureKey() {
        val localClosure = LocalClosure { EmptyDispatchClosure }
        localClosure.find(LocalClosure) shouldBe localClosure
    }

    @Test
    fun findShouldReturnValueFromCurrentWhenKeyIsNotLocalClosure() {
        val testClosure = TestDispatchClosure()
        val localClosure =  LocalClosure { testClosure }
        localClosure.find(TestDispatchClosure) shouldBe testClosure
    }

    @Test
    fun findShouldReturnNullWhenWantedKeyIsNotPresent() {
        val localClosure =  LocalClosure { EmptyDispatchClosure }
        localClosure.find(TestDispatchClosure) shouldBe null
    }

    @Test
    fun getShouldReturnLocalClosureItselfWhenCalledWithLocalClosureKey() {
        val localClosure =  LocalClosure { EmptyDispatchClosure }
        localClosure[LocalClosure] shouldBe localClosure
    }

    @Test
    fun getShouldReturnValueFromCurrentWhenKeyIsNotLocalClosure() {
        val testClosure = TestDispatchClosure()
        val localClosure = LocalClosure { testClosure }
        localClosure[TestDispatchClosure] shouldBe testClosure
    }

    @Test
    fun getShouldThrowMissingClosureElementExceptionWhenWantedKeyIsNotPresent() {
        val localClosure =  LocalClosure { EmptyDispatchClosure }
        shouldThrow<MissingClosureElementException> { localClosure[TestDispatchClosure] }
    }

    @Test
    fun withLocalClosureShouldProvideLocalClosureProperly() {
        val scope = dispatchScope(LocalClosure { EmptyDispatchClosure }, {}, {})
        val localClosure = TestDispatchClosure()
        scope.withLocalClosure(localClosure) {
            closure[TestDispatchClosure] shouldBe localClosure
        }
    }

    @Test
    fun withLocalClosureShouldRevertLocalClosureProperly() {
        val scope = dispatchScope(LocalClosure { EmptyDispatchClosure }, {}, {})
        scope.withLocalClosure(TestDispatchClosure()) { }
        scope.closure.find(TestDispatchClosure) shouldBe null
    }
}