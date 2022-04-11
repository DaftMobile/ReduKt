package com.daftmobile.redukt.core.context

import com.daftmobile.redukt.core.TestDispatchContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

internal class EmptyDispatchContextTest {

    @Test
    fun findShouldReturnNull() {
        assertNull(EmptyDispatchContext.find(TestDispatchContext))
    }

    @Test
    fun getShouldReturnThrowMissingContextElementException() {
        assertFailsWith<MissingContextElementException> {
            EmptyDispatchContext[TestDispatchContext]
        }
    }

    @Test
    fun splitShouldReturnEmptyList() {
        assertEquals(emptyList(), EmptyDispatchContext.split())
    }

    @Test
    fun plusOperatorShouldReturnRightSide() {
        val rightSide = TestDispatchContext()
        assertEquals(rightSide, EmptyDispatchContext + rightSide)
    }
}