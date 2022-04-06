package com.github.lupuuss.redukt.core.context

import com.github.lupuuss.redukt.core.TestDispatchContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class DispatchContextElementTest {

    private val dispatchContextElement = TestDispatchContext()


    @Test
    fun findShouldReturnThisWhenCompanionIsPassed() {
        assertEquals(dispatchContextElement, dispatchContextElement.find(TestDispatchContext))
    }

    @Test
    fun getShouldReturnThisWhenCompanionIsPassed() {
        assertEquals(dispatchContextElement, dispatchContextElement[TestDispatchContext])
    }

    @Test
    fun findShouldReturnNullWhenOtherKeyIsPassed() {
        assertNull(dispatchContextElement.find(ContextElementB))
    }

    @Test
    fun getShouldThrowMissingContextElementExceptionWhenAnyOtherKeyIsPassed() {
        assertFailsWith<MissingContextElementException> {
            dispatchContextElement[ContextElementB]
        }
    }

    @Test
    fun splitShouldReturnListWithOnlyThis() {
        assertEquals(listOf(dispatchContextElement), dispatchContextElement.split())
    }
}