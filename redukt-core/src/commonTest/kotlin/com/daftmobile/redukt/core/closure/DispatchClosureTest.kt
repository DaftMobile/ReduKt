package com.daftmobile.redukt.core.closure

import com.daftmobile.redukt.core.ClosureElementA
import com.daftmobile.redukt.core.ClosureElementB
import com.daftmobile.redukt.core.ClosureElementC
import io.kotest.assertions.throwables.shouldThrowUnit
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import kotlin.test.Test

private class ControlDispatchClosure(
    private val findResult: Any? = null,
    private val scatterResult: Map<DispatchClosure.Key<*>, DispatchClosure.Element> = emptyMap()
) : DispatchClosure {
    @Suppress("UNCHECKED_CAST")
    override fun <T : DispatchClosure.Element> find(key: DispatchClosure.Key<T>): T? = findResult as? T
    override fun scatter(): Map<DispatchClosure.Key<*>, DispatchClosure.Element> = scatterResult
}

internal class DispatchClosureTest {

    @Test
    fun getShouldUseFind() {
        val findResult = ClosureElementA()
        val closure = ControlDispatchClosure(findResult = findResult)
        closure[ClosureElementA] shouldBe findResult
    }

    @Test
    fun findOrElseShouldReturnFindResultWhenPresent() {
        val findClosure = ClosureElementA()
        val elseClosure = ClosureElementA()
        val closure = ControlDispatchClosure(findResult = findClosure)
        closure.findOrElse(ClosureElementA, elseClosure) shouldBe findClosure
    }

    @Test
    fun findOrElseShouldReturnElseValueWhenFindReturnsNull() {
        val elseClosure = ClosureElementA()
        val closure = ControlDispatchClosure(findResult = null)
        closure.findOrElse(ClosureElementA, elseClosure) shouldBe elseClosure
    }

    @Test
    fun getShouldThrowMissingClosureElementWhenItIsNotPresent() {
        val closure = ControlDispatchClosure(findResult = null)
        shouldThrowUnit<MissingClosureElementException> {
            closure[ClosureElementA]
        }
    }

    @Test
    fun plusResultClosureShouldBeAbleToFindAllProducts() {
        val elementA = ClosureElementA()
        val closure1 = ControlDispatchClosure(scatterResult = mapOf(elementA.key to elementA))
        val elementB = ClosureElementB()
        val closure2 = ControlDispatchClosure(scatterResult = mapOf(elementB.key to elementB))
        val resultClosure = closure1 + closure2
        resultClosure.find(ClosureElementA) shouldBe elementA
        resultClosure.find(ClosureElementB) shouldBe elementB
    }

    @Test
    fun plusResultClosureShouldBeAbleToScatterIntoItsProducts() {
        val elementA = ClosureElementA()
        val closure1 = ControlDispatchClosure(scatterResult = mapOf(elementA.key to elementA))
        val elementB = ClosureElementB()
        val closure2 = ControlDispatchClosure(scatterResult = mapOf(elementB.key to elementB))
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
    fun repeatedPlusShouldResultInFlatElementsListWhenScattered() {
        val elements = listOf(ClosureElementA(), ClosureElementB(), ClosureElementC())
        val result = elements[0] + elements[1] + elements[2]
        result.scatter() shouldBe elements.associateBy { it.key }
    }
}