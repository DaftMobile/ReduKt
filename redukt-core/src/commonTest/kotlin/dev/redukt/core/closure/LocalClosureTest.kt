package dev.redukt.core.closure

import dev.redukt.core.ClosureElementA
import dev.redukt.core.ClosureElementB
import dev.redukt.core.dispatchScope
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldNotBeIn
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.test.Test

class LocalClosureTest {

    @Test
    fun shouldDelegateToBaseClosureWhenLocalNotPresent() {
        val baseClosure = ClosureElementA()
        val closure = LocalClosure { baseClosure }
        closure[ClosureElementA] shouldBe baseClosure
    }

    @Test
    fun shouldReturnLocalClosureWhenLocalPresent() {
        val baseClosure = ClosureElementA()
        val localClosure = ClosureElementA()
        val closure = LocalClosure { baseClosure }
        closure.registerNewSlot(localClosure)
        closure[ClosureElementA] shouldBe localClosure
        closure[ClosureElementA] shouldNotBe baseClosure
    }

    @Test
    fun shouldReturnLatestLocalClosureWhenMultipleLocalsPresent() {
        val baseClosure = ClosureElementA()
        val firstLocalClosure = ClosureElementA()
        val secondLocalClosure = ClosureElementA()
        val closure = LocalClosure { baseClosure }
        closure.registerNewSlot(firstLocalClosure)
        closure.registerNewSlot(secondLocalClosure)
        closure[ClosureElementA] shouldBe secondLocalClosure
        closure[ClosureElementA] shouldNotBeIn arrayOf(baseClosure, firstLocalClosure)
    }

    @Test
    fun shouldAccumulateValuesFromPreviousLocalClosuresToLatestOne() {
        val baseClosure = ClosureElementB()
        val closureElementA =  ClosureElementA()
        val firstLocalClosure = ClosureElementB() + closureElementA
        val secondLocalClosure = ClosureElementB()
        val closure = LocalClosure { baseClosure }
        closure.registerNewSlot(firstLocalClosure)
        closure.registerNewSlot(secondLocalClosure)
        closure[ClosureElementA] shouldBe closureElementA
    }

    @Test
    fun shouldReturnNextAvailableLocalClosureWhenLocalSlotRemoved() {
        val baseClosure = ClosureElementA()
        val firstLocalClosure = ClosureElementA()
        val secondLocalClosure = ClosureElementA()
        val closure = LocalClosure { baseClosure }
        closure.registerNewSlot(firstLocalClosure)
        closure.registerNewSlot(secondLocalClosure).also(closure::removeSlot)
        closure[ClosureElementA] shouldBe firstLocalClosure
        closure[ClosureElementA] shouldNotBeIn arrayOf(baseClosure, secondLocalClosure)
    }

    @Test
    fun shouldReturnBaseClosureWhenAllLocalSlotsRemoved() {
        val baseClosure = ClosureElementA()
        val firstLocalClosure = ClosureElementA()
        val closure = LocalClosure { baseClosure }
        closure.registerNewSlot(firstLocalClosure).also(closure::removeSlot)
        closure[ClosureElementA] shouldBe baseClosure
        closure[ClosureElementA] shouldNotBe firstLocalClosure
    }

    @Test
    fun findShouldReturnLocalClosureItselfWhenCalledWithLocalClosureKey() {
        val localClosure = LocalClosure { EmptyDispatchClosure }
        localClosure.find(LocalClosure) shouldBe localClosure
    }

    @Test
    fun findShouldReturnValueFromCurrentWhenKeyIsNotLocalClosure() {
        val testClosure = ClosureElementA()
        val localClosure =  LocalClosure { testClosure }
        localClosure.find(ClosureElementA) shouldBe testClosure
    }

    @Test
    fun findShouldReturnNullWhenWantedKeyIsNotPresent() {
        val localClosure =  LocalClosure { EmptyDispatchClosure }
        localClosure.find(ClosureElementA) shouldBe null
    }

    @Test
    fun getShouldReturnLocalClosureItselfWhenCalledWithLocalClosureKey() {
        val localClosure =  LocalClosure { EmptyDispatchClosure }
        localClosure[LocalClosure] shouldBe localClosure
    }

    @Test
    fun getShouldReturnValueFromCurrentWhenKeyIsNotLocalClosure() {
        val testClosure = ClosureElementA()
        val localClosure = LocalClosure { testClosure }
        localClosure[ClosureElementA] shouldBe testClosure
    }

    @Test
    fun getShouldThrowMissingClosureElementExceptionWhenWantedKeyIsNotPresent() {
        val localClosure =  LocalClosure { EmptyDispatchClosure }
        shouldThrow<MissingClosureElementException> { localClosure[ClosureElementA] }
    }

    @Test
    fun withLocalClosureShouldProvideLocalClosureProperly() {
        val scope = dispatchScope(LocalClosure { EmptyDispatchClosure }, {}, {})
        val localClosure = ClosureElementA()
        scope.withLocalClosure(localClosure) {
            closure[ClosureElementA] shouldBe localClosure
        }
    }

    @Test
    fun withLocalClosureShouldRevertLocalClosureProperly() {
        val scope = dispatchScope(LocalClosure { EmptyDispatchClosure }, {}, {})
        scope.withLocalClosure(ClosureElementA()) { }
        scope.closure.find(ClosureElementA) shouldBe null
    }
}