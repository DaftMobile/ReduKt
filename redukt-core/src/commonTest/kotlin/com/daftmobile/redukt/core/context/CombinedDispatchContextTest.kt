package com.daftmobile.redukt.core.context

import com.daftmobile.redukt.core.TestDispatchContext
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class CombinedDispatchContextTest {

    @Test
    fun findShouldReturnElementFromPassedElements() {
        val elements = listOf(ContextElementA(), ContextElementB())
        val context = CombinedDispatchContext(elements)
        context.find(ContextElementB) shouldBe elements[1]
    }

    @Test
    fun findShouldReturnNullWhenElementWithPassedKeyIsMissing() {
        val elements = listOf(ContextElementA(), ContextElementB())
        val context = CombinedDispatchContext(elements)
        context.find(TestDispatchContext).shouldBeNull()
    }

    @Test
    fun splitShouldReturnElementsPassedToConstructor() {
        val elements = listOf(ContextElementA(), ContextElementB())
        val context = CombinedDispatchContext(elements)
        context.split() shouldBe elements
    }

    @Test
    fun constructorShouldFailWhenEmptyListPassed() {
        shouldThrowAny {
            CombinedDispatchContext(emptyList())
        }
    }
}