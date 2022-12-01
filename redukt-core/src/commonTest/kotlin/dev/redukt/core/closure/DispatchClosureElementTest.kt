package dev.redukt.core.closure

import dev.redukt.core.ClosureElementA
import dev.redukt.core.ClosureElementB
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrowUnit
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class DispatchClosureElementTest {

    private val dispatchClosureElement = ClosureElementA()

    @Test
    fun findShouldReturnThisWhenCompanionIsPassed() {
        dispatchClosureElement.find(ClosureElementA) shouldBe dispatchClosureElement
    }

    @Test
    fun getShouldReturnThisWhenCompanionIsPassed() {
        shouldNotThrowAny {
            dispatchClosureElement[ClosureElementA] shouldBe dispatchClosureElement
        }
    }

    @Test
    fun findShouldReturnNullWhenOtherKeyIsPassed() {
        dispatchClosureElement.find(ClosureElementB).shouldBeNull()
    }

    @Test
    fun getShouldThrowMissingClosureElementExceptionWhenAnyOtherKeyIsPassed() {
        shouldThrowUnit<MissingClosureElementException> {
            dispatchClosureElement[ClosureElementB]
        }
    }

    @Test
    fun scatterShouldReturnListWithOnlyThis() {
        dispatchClosureElement.scatter() shouldContainExactly  mapOf(dispatchClosureElement.key to dispatchClosureElement)
    }

    @Test
    fun minusShouldReturnThisWhenKeyDiffers() {
        (dispatchClosureElement - ClosureElementB) shouldBe dispatchClosureElement
    }

    @Test
    fun minusShouldReturnEmptyClosureWhenKeysDiffer() {
        (dispatchClosureElement - ClosureElementA) shouldBe EmptyDispatchClosure
    }
}