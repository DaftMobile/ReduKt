package com.daftmobile.redukt.core.context

import com.daftmobile.redukt.core.TestDispatchContext
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
    fun getShouldUseFind() {
        val findResult = TestDispatchContext()
        val context = ControlDispatchContext(findResult = findResult)
        assertEquals(findResult, context[TestDispatchContext])
    }

    @Test
    fun getShouldThrowMissingContextElementWhenItIsNotPresent() {
        val context = ControlDispatchContext(findResult = null)
        assertFailsWith<MissingContextElementException> {
            context[TestDispatchContext]
        }
    }

    @Test
    fun plusResultContextShouldBeAbleToFindAllProducts() {
        val elementA = ContextElementA()
        val context1 = ControlDispatchContext(splitResult = listOf(elementA))
        val elementB = ContextElementB()
        val context2 = ControlDispatchContext(splitResult = listOf(elementB))
        val resultContext = context1 + context2
        assertEquals(elementA, resultContext.find(ContextElementA))
        assertEquals(elementB, resultContext.find(ContextElementB))
    }

    @Test
    fun plusResultContextShouldBeAbleToSplitIntoItsProducts() {
        val elementA = ContextElementA()
        val context1 = ControlDispatchContext(splitResult = listOf(elementA))
        val elementB = ContextElementB()
        val context2 = ControlDispatchContext(splitResult = listOf(elementB))
        val resultContext = context1 + context2
        assertEquals(listOf(elementA, elementB), resultContext.split())
    }

    @Test
    fun plusShouldBeRightSideBiasedWhenSameKeyElementsOverwriteEachOther() {
        val elementA = ContextElementA()
        val anotherElementA = ContextElementA()
        val resultContext = elementA + anotherElementA
        assertSame(anotherElementA, resultContext.find(ContextElementA))
    }

    @Test
    fun repeatedPlusShouldResultInFlatElementsListWhenSplitted() {
        val elements = listOf(ContextElementA(), ContextElementB(), TestDispatchContext())
        val result = elements[0] + elements[1] + elements[2]
        assertEquals(elements, result.split())
    }
}