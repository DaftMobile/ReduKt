package com.github.lupuuss.redukt.core.context

import com.github.lupuuss.redukt.core.TestDispatchContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNull

internal class CombinedDispatchContextTest {

    @Test
    fun `should find element with a given key from passed elements `() {
        val elements = listOf(ContextElementA(), ContextElementB())
        val context = CombinedDispatchContext(elements)
        assertEquals(elements[1] as ContextElementB, context.find(ContextElementB))
    }

    @Test
    fun `should find null when element with given key is absent`() {
        val elements = listOf(ContextElementA(), ContextElementB())
        val context = CombinedDispatchContext(elements)
        assertNull(context.find(TestDispatchContext))
    }

    @Test
    fun `should split into elements passed through constructor`() {
        val elements = listOf(ContextElementA(), ContextElementB())
        val context = CombinedDispatchContext(elements)
        assertEquals(elements, context.split())
    }

    @Test
    fun `should fail when no elements passed on creation`() {
        assertFails {
            CombinedDispatchContext(emptyList())
        }
    }
}