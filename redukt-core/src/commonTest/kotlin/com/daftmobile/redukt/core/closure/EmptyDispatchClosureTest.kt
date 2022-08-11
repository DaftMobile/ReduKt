package com.daftmobile.redukt.core.closure

import com.daftmobile.redukt.core.TestDispatchClosure
import io.kotest.assertions.throwables.shouldThrowUnit
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class EmptyDispatchClosureTest {

    @Test
    fun findShouldReturnNull() {
        EmptyDispatchClosure
            .find(TestDispatchClosure)
            .shouldBeNull()
    }

    @Test
    fun getShouldReturnThrowMissingClosureElementException() {
        shouldThrowUnit<MissingClosureElementException> {
            EmptyDispatchClosure[TestDispatchClosure]
        }
    }

    @Test
    fun splitShouldReturnEmptyList() {
        EmptyDispatchClosure.split().shouldBeEmpty()
    }

    @Test
    fun plusOperatorShouldReturnRightSide() {
        val rightSide = TestDispatchClosure()
        (EmptyDispatchClosure + rightSide) shouldBe rightSide
    }
}