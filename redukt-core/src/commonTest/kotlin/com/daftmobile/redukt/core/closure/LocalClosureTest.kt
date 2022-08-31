package com.daftmobile.redukt.core.closure

import com.daftmobile.redukt.core.CoreDispatchScope
import com.daftmobile.redukt.core.TestDispatchClosure
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlin.test.Test

private class ControlLocalClosure(override val current: DispatchClosure) : LocalClosure {

    override fun registerNewSlot(closure: DispatchClosure): LocalSlot = LocalSlot()

    override fun removeSlot(slot: LocalSlot) = Unit

}

class LocalClosureTest {

    @Test
    fun findShouldReturnLocalClosureItselfWhenCalledWithLocalClosureKey() {
        val localClosure = ControlLocalClosure(EmptyDispatchClosure)
        localClosure.find(LocalClosure) shouldBe localClosure
    }

    @Test
    fun findShouldReturnValueFromCurrentWhenKeyIsNotLocalClosure() {
        val testClosure = TestDispatchClosure()
        val localClosure = ControlLocalClosure(testClosure)
        localClosure.find(TestDispatchClosure) shouldBe testClosure
    }

    @Test
    fun findShouldReturnNullWhenWantedKeyIsNotPresent() {
        val localClosure = ControlLocalClosure(EmptyDispatchClosure)
        localClosure.find(TestDispatchClosure) shouldBe null
    }

    @Test
    fun getShouldReturnLocalClosureItselfWhenCalledWithLocalClosureKey() {
        val localClosure = ControlLocalClosure(EmptyDispatchClosure)
        localClosure[LocalClosure] shouldBe localClosure
    }

    @Test
    fun getShouldReturnValueFromCurrentWhenKeyIsNotLocalClosure() {
        val testClosure = TestDispatchClosure()
        val localClosure = ControlLocalClosure(testClosure)
        localClosure[TestDispatchClosure] shouldBe testClosure
    }

    @Test
    fun getShouldThrowMissingClosureElementExceptionWhenWantedKeyIsNotPresent() {
        val localClosure = ControlLocalClosure(EmptyDispatchClosure)
        shouldThrow<MissingClosureElementException> { localClosure[TestDispatchClosure] }
    }

    @Test
    fun withLocalClosureShouldProvideLocalClosureProperly() {
        val scope = CoreDispatchScope(CoreLocalClosure { EmptyDispatchClosure }, {}, {})
        val localClosure = TestDispatchClosure()
        scope.withLocalClosure(localClosure) {
            closure[TestDispatchClosure] shouldBe localClosure
        }
    }

    @Test
    fun withLocalClosureShouldRevertLocalClosureProperly() {
        val scope = CoreDispatchScope(CoreLocalClosure { EmptyDispatchClosure }, {}, {})
        scope.withLocalClosure(TestDispatchClosure()) { }
        scope.closure.find(TestDispatchClosure) shouldBe null
    }
}