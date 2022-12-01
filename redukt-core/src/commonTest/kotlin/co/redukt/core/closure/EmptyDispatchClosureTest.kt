package co.redukt.core.closure

import co.redukt.core.ClosureElementA
import io.kotest.assertions.throwables.shouldThrowUnit
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class EmptyDispatchClosureTest {

    @Test
    fun findShouldReturnNull() {
        EmptyDispatchClosure
            .find(ClosureElementA)
            .shouldBeNull()
    }

    @Test
    fun getShouldReturnThrowMissingClosureElementException() {
        shouldThrowUnit<MissingClosureElementException> {
            EmptyDispatchClosure[ClosureElementA]
        }
    }

    @Test
    fun splitShouldReturnEmptyList() {
        EmptyDispatchClosure.scatter().shouldBeEmpty()
    }

    @Test
    fun plusOperatorShouldReturnRightSide() {
        val rightSide = ClosureElementA()
        (EmptyDispatchClosure + rightSide) shouldBe rightSide
    }

    @Test
    fun minusAnyKeyShouldReturnThis() {
        (EmptyDispatchClosure - ClosureElementA) shouldBe EmptyDispatchClosure
    }
}