package com.daftmobile.redukt.core.context

import com.daftmobile.redukt.core.TestDispatchContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNull

internal class CombinedDispatchContextTest {

    @Test
    fun findShouldReturnElementFromPassedElements() {
        val elements = listOf(ContextElementA(), ContextElementB())
        val context = CombinedDispatchContext(elements)
        assertEquals(elements[1] as ContextElementB, context.find(ContextElementB))
    }

    @Test
    fun findShouldReturnNullWhenElementWithPassedKeyIsMissing() {
        val elements = listOf(ContextElementA(), ContextElementB())
        val context = CombinedDispatchContext(elements)
        assertNull(context.find(TestDispatchContext))
    }

    @Test
    fun splitShouldReturnElementsPassedToConstructor() {
        val elements = listOf(ContextElementA(), ContextElementB())
        val context = CombinedDispatchContext(elements)
        assertEquals(elements, context.split())
    }

    @Test
    fun constructorShouldFailWhenEmptyListPassed() {
        assertFails {
            CombinedDispatchContext(emptyList())
        }
    }
}