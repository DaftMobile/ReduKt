package com.github.lupuuss.redukt.core.context

import com.github.lupuuss.redukt.core.TestDispatchContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

private class ControlDispatchContext(
    private val findResult: Any? = null,
    private val splitResult: List<DispatchContext.Element> = emptyList()
) : DispatchContext {
    @Suppress("UNCHECKED_CAST")
    override fun <T : DispatchContext.Element> find(key: DispatchContext.Key<T>): T? = findResult as? T
    override fun split(): List<DispatchContext.Element> = splitResult
}

internal class DispatchContextTest {

    @Test
    fun `should use find result to get an element`() {
        val findResult = TestDispatchContext()
        val context = ControlDispatchContext(findResult = findResult)
        assertEquals(findResult, context[TestDispatchContext])
    }

    @Test
    fun `should throw MissingContextElementException when element cannot be found`() {
        val context = ControlDispatchContext(findResult = null)
        assertFailsWith<MissingContextElementException> {
            context[TestDispatchContext]
        }
    }

    @Test
    fun `plus result should allow to find each element of origin contexts`() {
        val elementA = ContextElementA()
        val context1 = ControlDispatchContext(splitResult = listOf(elementA))
        val elementB = ContextElementB()
        val context2 = ControlDispatchContext(splitResult = listOf(elementB))
        val resultContext = context1 + context2
        assertEquals(elementA, resultContext.find(ContextElementA))
        assertEquals(elementB, resultContext.find(ContextElementB))
    }

    @Test
    fun `plus result should return elements of origin contexts on split`() {
        val elementA = ContextElementA()
        val context1 = ControlDispatchContext(splitResult = listOf(elementA))
        val elementB = ContextElementB()
        val context2 = ControlDispatchContext(splitResult = listOf(elementB))
        val resultContext = context1 + context2
        assertEquals(listOf(elementA, elementB), resultContext.split())
    }

    @Test
    fun `plus result should be right side biased, when overriding context elements`() {
        val elementA = ContextElementA()
        val anotherElementA = ContextElementA()
        val resultContext = elementA + anotherElementA
        assertSame(anotherElementA, resultContext.find(ContextElementA))
    }

    @Test
    fun `repeated plus should result in flat elements list`() {
        val elements = listOf(ContextElementA(), ContextElementB(), TestDispatchContext())
        val result = elements[0] + elements[1] + elements[2]
        assertEquals(elements, result.split())
    }
}