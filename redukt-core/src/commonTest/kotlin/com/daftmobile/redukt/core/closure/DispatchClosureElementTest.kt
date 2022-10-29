package com.daftmobile.redukt.core.closure

import com.daftmobile.redukt.core.TestDispatchClosure
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrowUnit
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class DispatchClosureElementTest {

    private val dispatchClosureElement = TestDispatchClosure()

    @Test
    fun findShouldReturnThisWhenCompanionIsPassed() {
        dispatchClosureElement.find(TestDispatchClosure) shouldBe dispatchClosureElement
    }

    @Test
    fun getShouldReturnThisWhenCompanionIsPassed() {
        shouldNotThrowAny {
            dispatchClosureElement[TestDispatchClosure] shouldBe dispatchClosureElement
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
    fun splitShouldReturnListWithOnlyThis() {
        dispatchClosureElement.scatter() shouldContainExactly  mapOf(dispatchClosureElement.key to dispatchClosureElement)
    }
}