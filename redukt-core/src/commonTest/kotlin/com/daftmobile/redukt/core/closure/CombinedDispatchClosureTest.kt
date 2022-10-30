package com.daftmobile.redukt.core.closure

import com.daftmobile.redukt.core.ClosureElementA
import com.daftmobile.redukt.core.ClosureElementB
import com.daftmobile.redukt.core.ClosureElementC
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class CombinedDispatchClosureTest {

    @Test
    fun findShouldReturnElementFromPassedElements() {
        val elements = mapOf(ClosureElementA.Key to ClosureElementA(), ClosureElementB.Key to ClosureElementB())
        val closure = CombinedDispatchClosure(elements)
        closure.find(ClosureElementA) shouldBe elements[ClosureElementA.Key]
        closure.find(ClosureElementB) shouldBe elements[ClosureElementB.Key]
    }

    @Test
    fun findShouldReturnNullWhenElementWithPassedKeyIsMissing() {
        val elements = mapOf(ClosureElementA.Key to ClosureElementA(), ClosureElementB.Key to ClosureElementB())
        val closure = CombinedDispatchClosure(elements)
        closure.find(ClosureElementC).shouldBeNull()
    }

    @Test
    fun scatterShouldReturnElementsPassedToConstructor() {
        val elements = mapOf(ClosureElementA.Key to ClosureElementA(), ClosureElementB.Key to ClosureElementB())
        val closure = CombinedDispatchClosure(elements)
        closure.scatter() shouldBe elements
    }

    @Test
    fun constructorShouldFailWhenEmptyListPassed() {
        shouldThrowAny {
            CombinedDispatchClosure(emptyMap())
        }
    }
}