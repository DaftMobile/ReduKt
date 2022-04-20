package com.daftmobile.redukt.core.context

import com.daftmobile.redukt.core.TestDispatchContext
import io.kotest.assertions.throwables.shouldThrowUnit
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import kotlin.test.Test

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
        context[TestDispatchContext] shouldBe findResult
    }

    @Test
    fun getShouldThrowMissingContextElementWhenItIsNotPresent() {
        val context = ControlDispatchContext(findResult = null)
        shouldThrowUnit<MissingContextElementException> {
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
        resultContext.find(ContextElementA) shouldBe elementA
        resultContext.find(ContextElementB) shouldBe elementB
    }

    @Test
    fun plusResultContextShouldBeAbleToSplitIntoItsProducts() {
        val elementA = ContextElementA()
        val context1 = ControlDispatchContext(splitResult = listOf(elementA))
        val elementB = ContextElementB()
        val context2 = ControlDispatchContext(splitResult = listOf(elementB))
        val resultContext = context1 + context2
        resultContext.split() shouldBe listOf(elementA, elementB)
    }

    @Test
    fun plusShouldBeRightSideBiasedWhenSameKeyElementsOverwriteEachOther() {
        val elementA = ContextElementA()
        val anotherElementA = ContextElementA()
        val resultContext = elementA + anotherElementA
        resultContext.find(ContextElementA) shouldBeSameInstanceAs anotherElementA
    }

    @Test
    fun repeatedPlusShouldResultInFlatElementsListWhenSplitted() {
        val elements = listOf(ContextElementA(), ContextElementB(), TestDispatchContext())
        val result = elements[0] + elements[1] + elements[2]
        result.split() shouldBe elements
    }
}