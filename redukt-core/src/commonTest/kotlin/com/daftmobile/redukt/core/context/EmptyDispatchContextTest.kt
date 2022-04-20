package com.daftmobile.redukt.core.context

import com.daftmobile.redukt.core.TestDispatchContext
import io.kotest.assertions.throwables.shouldThrowUnit
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class EmptyDispatchContextTest {

    @Test
    fun findShouldReturnNull() {
        EmptyDispatchContext
            .find(TestDispatchContext)
            .shouldBeNull()
    }

    @Test
    fun getShouldReturnThrowMissingContextElementException() {
        shouldThrowUnit<MissingContextElementException> {
            EmptyDispatchContext[TestDispatchContext]
        }
    }

    @Test
    fun splitShouldReturnEmptyList() {
        EmptyDispatchContext.split().shouldBeEmpty()
    }

    @Test
    fun plusOperatorShouldReturnRightSide() {
        val rightSide = TestDispatchContext()
        (EmptyDispatchContext + rightSide) shouldBe rightSide
    }
}