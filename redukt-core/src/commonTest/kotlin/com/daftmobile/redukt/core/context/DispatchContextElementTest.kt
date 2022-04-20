package com.daftmobile.redukt.core.context

import com.daftmobile.redukt.core.TestDispatchContext
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrowUnit
import io.kotest.matchers.collections.shouldHaveSingleElement
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class DispatchContextElementTest {

    private val dispatchContextElement = TestDispatchContext()

    @Test
    fun findShouldReturnThisWhenCompanionIsPassed() {
        dispatchContextElement.find(TestDispatchContext) shouldBe dispatchContextElement
    }

    @Test
    fun getShouldReturnThisWhenCompanionIsPassed() {
        shouldNotThrowAny {
            dispatchContextElement[TestDispatchContext] shouldBe dispatchContextElement
        }
    }

    @Test
    fun findShouldReturnNullWhenOtherKeyIsPassed() {
        dispatchContextElement.find(ContextElementB).shouldBeNull()
    }

    @Test
    fun getShouldThrowMissingContextElementExceptionWhenAnyOtherKeyIsPassed() {
        shouldThrowUnit<MissingContextElementException> {
            dispatchContextElement[ContextElementB]
        }
    }

    @Test
    fun splitShouldReturnListWithOnlyThis() {
        dispatchContextElement.split() shouldHaveSingleElement dispatchContextElement
    }
}