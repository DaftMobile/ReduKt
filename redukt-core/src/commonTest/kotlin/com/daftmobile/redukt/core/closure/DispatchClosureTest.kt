package com.daftmobile.redukt.core.closure

import com.daftmobile.redukt.core.TestDispatchClosure
import io.kotest.assertions.throwables.shouldThrowUnit
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import kotlin.test.Test

private class ControlDispatchClosure(
    private val findResult: Any? = null,
    private val splitResult: List<DispatchClosure.Element> = emptyList()
) : DispatchClosure {
    @Suppress("UNCHECKED_CAST")
    override fun <T : DispatchClosure.Element> find(key: DispatchClosure.Key<T>): T? = findResult as? T
    override fun split(): List<DispatchClosure.Element> = splitResult
}

internal class DispatchClosureTest {

    @Test
    fun getShouldUseFind() {
        val findResult = TestDispatchClosure()
        val closure = ControlDispatchClosure(findResult = findResult)
        closure[TestDispatchClosure] shouldBe findResult
    }

    @Test
    fun getShouldThrowMissingClosureElementWhenItIsNotPresent() {
        val closure = ControlDispatchClosure(findResult = null)
        shouldThrowUnit<MissingClosureElementException> {
            closure[TestDispatchClosure]
        }
    }

    @Test
    fun plusResultClosureShouldBeAbleToFindAllProducts() {
        val elementA = ClosureElementA()
        val closure1 = ControlDispatchClosure(splitResult = listOf(elementA))
        val elementB = ClosureElementB()
        val closure2 = ControlDispatchClosure(splitResult = listOf(elementB))
        val resultClosure = closure1 + closure2
        resultClosure.find(ClosureElementA) shouldBe elementA
        resultClosure.find(ClosureElementB) shouldBe elementB
    }

    @Test
    fun plusResultClosureShouldBeAbleToSplitIntoItsProducts() {
        val elementA = ClosureElementA()
        val closure1 = ControlDispatchClosure(splitResult = listOf(elementA))
        val elementB = ClosureElementB()
        val closure2 = ControlDispatchClosure(splitResult = listOf(elementB))
        val resultClosure = closure1 + closure2
        resultClosure.split() shouldBe listOf(elementA, elementB)
    }

    @Test
    fun plusShouldBeRightSideBiasedWhenSameKeyElementsOverwriteEachOther() {
        val elementA = ClosureElementA()
        val anotherElementA = ClosureElementA()
        val resultClosure = elementA + anotherElementA
        resultClosure.find(ClosureElementA) shouldBeSameInstanceAs anotherElementA
    }

    @Test
    fun repeatedPlusShouldResultInFlatElementsListWhenSplitted() {
        val elements = listOf(ClosureElementA(), ClosureElementB(), TestDispatchClosure())
        val result = elements[0] + elements[1] + elements[2]
        result.split() shouldBe elements
    }
}