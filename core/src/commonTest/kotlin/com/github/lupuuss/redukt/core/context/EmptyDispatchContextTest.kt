package com.github.lupuuss.redukt.core.context

import com.github.lupuuss.redukt.core.TestDispatchContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

internal class EmptyDispatchContextTest {

    @Test
    fun `should return null on find`() {
        assertNull(EmptyDispatchContext.find(TestDispatchContext))
    }

    @Test
    fun `should return throw MissingContextElementException on get`() {
        assertFailsWith<MissingContextElementException> {
            EmptyDispatchContext[TestDispatchContext]
        }
    }

    @Test
    fun `should split into empty list`() {
        assertEquals(emptyList(), EmptyDispatchContext.split())
    }

    @Test
    fun `should return right side on plus operator`() {
        val rightSide = TestDispatchContext()
        assertEquals(rightSide, EmptyDispatchContext + rightSide)
    }
}