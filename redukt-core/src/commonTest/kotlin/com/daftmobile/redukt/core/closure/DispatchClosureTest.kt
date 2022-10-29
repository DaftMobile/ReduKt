package com.daftmobile.redukt.core.closure

import com.daftmobile.redukt.core.TestDispatchClosure
import io.kotest.assertions.throwables.shouldThrowUnit
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import kotlin.test.Test

private class ControlDispatchClosure(
    private val findResult: Any? = null,
    private val splitResult: Map<DispatchClosure.Key<*>, DispatchClosure.Element> = emptyMap()
) : DispatchClosure {
    @Suppress("UNCHECKED_CAST")
    override fun <T : DispatchClosure.Element> find(key: DispatchClosure.Key<T>): T? = findResult as? T
    override fun scatter(): Map<DispatchClosure.Key<*>, DispatchClosure.Element> = splitResult
}

internal class DispatchClosureTest {

    @Test
    fun getShouldUseFind() {
        val findResult = TestDispatchClosure()
        val closure = ControlDispatchClosure(findResult = findResult)
        closure[TestDispatchClosure] shouldBe findResult
    }

    @Test
    fun findOrElseShouldReturnFindResultWhenPresent() {
        val findClosure = TestDispatchClosure()
        val elseClosure = TestDispatchClosure()
        val closure = ControlDispatchClosure(findResult = findClosure)
        closure.findOrElse(TestDispatchClosure, elseClosure) shouldBe findClosure
    }

    @Test
    fun findOrElseShouldReturnElseValueWhenFindReturnsNull() {
        val elseClosure = TestDispatchClosure()
        val closure = ControlDispatchClosure(findResult = null)
        closure.findOrElse(TestDispatchClosure, elseClosure) shouldBe elseClosure
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
        val closure1 = ControlDispatchClosure(splitResult = mapOf(elementA.key to elementA))
        val elementB = ClosureElementB()
        val closure2 = ControlDispatchClosure(splitResult = mapOf(elementB.key to elementB))
        val resultClosure = closure1 + closure2
        resultClosure.find(ClosureElementA) shouldBe elementA
        resultClosure.find(ClosureElementB) shouldBe elementB
    }

    @Test
    fun plusResultClosureShouldBeAbleToSplitIntoItsProducts() {
        val elementA = ClosureElementA()
        val closure1 = ControlDispatchClosure(splitResult = mapOf(elementA.key to elementA))
        val elementB = ClosureElementB()
        val closure2 = ControlDispatchClosure(splitResult = mapOf(elementB.key to elementB))
        val resultClosure = closure1 + closure2
        resultClosure.scatter() shouldBe mapOf(elementA.key to elementA, elementB.key to elementB)
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
        result.scatter() shouldBe elements.associateBy { it.key }
    }
}