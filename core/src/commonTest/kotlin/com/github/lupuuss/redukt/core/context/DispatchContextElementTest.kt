package com.github.lupuuss.redukt.core.context

import com.github.lupuuss.redukt.core.TestDispatchContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class DispatchContextElementTest {

    private val dispatchContextElement = TestDispatchContext()


    @Test
    fun `should find itself on its key`() {
        assertEquals(dispatchContextElement, dispatchContextElement.find(TestDispatchContext))
    }

    @Test
    fun `should get itself on its key`() {
        assertEquals(dispatchContextElement, dispatchContextElement[TestDispatchContext])
    }

    @Test
    fun `should find null on any other key`() {
        assertNull(dispatchContextElement.find(ContextElementB))
    }

    @Test
    fun `should fail getting any other key`() {
        assertFailsWith<MissingContextElementException> {
            dispatchContextElement[ContextElementB]
        }
    }

    @Test
    fun `should split into list containing itself`() {
        assertEquals(listOf(dispatchContextElement), dispatchContextElement.split())
    }
}