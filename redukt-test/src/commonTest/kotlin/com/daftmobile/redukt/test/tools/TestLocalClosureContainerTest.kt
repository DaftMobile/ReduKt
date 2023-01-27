package com.daftmobile.redukt.test.tools

import com.daftmobile.redukt.core.closure.DispatchClosure
import com.daftmobile.redukt.core.closure.EmptyDispatchClosure
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.test.Test

internal class TestLocalClosureContainerTest {

    private val initialClosure = TestClosureElement()
    private val localClosureContainer = TestLocalClosureContainer(initialClosure)

    private class TestClosureElement : DispatchClosure.Element {
        override val key = Key

        companion object Key : DispatchClosure.Key<TestClosureElement>
    }

    @Test
    fun shouldNotApplyLocalOverwrites() {
        val closureElement = TestClosureElement()
        localClosureContainer.registerNewSlot(closureElement)
        localClosureContainer.applyTo(EmptyDispatchClosure).find(TestClosureElement) shouldNotBe closureElement
    }

    @Test
    fun shouldProvideInitialValues() {
        localClosureContainer.applyTo(EmptyDispatchClosure).find(TestClosureElement) shouldBe initialClosure
    }
}