package com.daftmobile.redukt.core.closure

import com.daftmobile.redukt.core.ClosureElementA
import com.daftmobile.redukt.core.ClosureElementB
import com.daftmobile.redukt.core.coroutines.DispatchCoroutineScope
import com.daftmobile.redukt.core.dispatchScope
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.test.Test

class LocalClosureContainerTest {

    @Test
    fun shouldDelegateToBaseClosureWhenLocalNotPresent() {
        val baseClosure = ClosureElementA()
        val closure = baseClosure + LocalClosureContainer()
        closure.local[ClosureElementA] shouldBe baseClosure
    }

    @Test
    fun shouldReturnLocalClosureWhenLocalPresent() {
        val baseClosure = ClosureElementA()
        val localChange = ClosureElementA()
        val closure = baseClosure + LocalClosureContainer()
        closure[LocalClosureContainer].registerNewSlot(localChange)
        closure.local[ClosureElementA] shouldBe localChange
    }

    @Test
    fun shouldReturnLatestLocalClosureWhenMultipleLocalsPresent() {
        val baseClosure = ClosureElementA()
        val firstLocalChange = ClosureElementA()
        val secondLocalChange = ClosureElementA()
        val closure = baseClosure + LocalClosureContainer()
        closure[LocalClosureContainer].registerNewSlot(firstLocalChange)
        closure[LocalClosureContainer].registerNewSlot(secondLocalChange)
        closure.local[ClosureElementA] shouldBe secondLocalChange
    }

    @Test
    fun shouldAccumulateValuesFromPreviousLocalClosuresToLatestOne() {
        val baseClosure = ClosureElementB()
        val closureElementA =  ClosureElementA()
        val firstLocalChange = ClosureElementB() + closureElementA
        val secondLocalChange = ClosureElementB()
        val closure = baseClosure + LocalClosureContainer()
        closure[LocalClosureContainer].registerNewSlot(firstLocalChange)
        closure[LocalClosureContainer].registerNewSlot(secondLocalChange)
        closure[LocalClosureContainer].registerNewSlot(LocalClosureContainer())
        closure.local[ClosureElementA] shouldBe closureElementA
    }

    @Test
    fun shouldReturnNextAvailableLocalClosureWhenLocalSlotRemoved() {
        val baseClosure = ClosureElementA()
        val firstLocalChange = ClosureElementA()
        val secondLocalChange = ClosureElementA()
        val localClosure = LocalClosureContainer()
        val closure = baseClosure + localClosure
        localClosure.registerNewSlot(firstLocalChange)
        localClosure.registerNewSlot(secondLocalChange).also(localClosure::removeSlot)
        closure.local[ClosureElementA] shouldBe firstLocalChange
    }

    @Test
    fun shouldReturnBaseClosureWhenAllLocalSlotsRemoved() {
        val baseClosure = ClosureElementA()
        val firstLocalChange = ClosureElementA()
        val localClosure = LocalClosureContainer()
        val closure = baseClosure + localClosure
        closure[LocalClosureContainer].registerNewSlot(firstLocalChange).also(localClosure::removeSlot)
        closure.local[ClosureElementA] shouldBe baseClosure
        closure.local[ClosureElementA] shouldNotBe firstLocalChange
    }

    @Test
    fun shouldSeparateLocalChangesInNewFrame() {
        val baseClosureA = ClosureElementA()
        val closure = baseClosureA + LocalClosureContainer()
        val localClosureA = ClosureElementA()
        closure[LocalClosureContainer].registerNewSlot(localClosureA)
        closure[LocalClosureContainer].registerNewFrame()
        closure.local[ClosureElementA] shouldBe baseClosureA
    }

    @Test
    fun shouldProvideLocalChangeAfterFrameRemoval() {
        val baseClosureA = ClosureElementA()
        val closure = baseClosureA + LocalClosureContainer()
        val localClosureA = ClosureElementA()
        closure[LocalClosureContainer].registerNewSlot(localClosureA)
        val frame = closure[LocalClosureContainer].registerNewFrame()
        closure[LocalClosureContainer].removeFrame(frame)
        closure.local[ClosureElementA] shouldBe localClosureA
    }

    @Test
    fun findShouldReturnThisWhenLocalClosureKey() {
        val localClosure = LocalClosureContainer()
        localClosure.find(LocalClosureContainer) shouldBe localClosure
    }

    @Test
    fun findShouldReturnNullWhenUnknownKey() {
        val closure = DispatchCoroutineScope(CoroutineScope(Dispatchers.Unconfined)) + LocalClosureContainer()
        closure[LocalClosureContainer].find(DispatchCoroutineScope) shouldBe null
    }

    @Test
    fun getShouldReturnThis() {
        val localClosure = LocalClosureContainer()
        localClosure[LocalClosureContainer] shouldBe localClosure
    }

    @Test
    fun getShouldThrowMissingClosureElementExceptionWhenUnknownKey() {
        val closure = DispatchCoroutineScope(CoroutineScope(Dispatchers.Unconfined)) + LocalClosureContainer()
        shouldThrow<MissingClosureElementException> { closure[LocalClosureContainer][DispatchCoroutineScope] }
    }

    @Test
    fun withLocalClosureShouldProvideLocalClosureProperly() {
        val scope = dispatchScope(LocalClosureContainer(), {}, {})
        val localClosure = ClosureElementA()
        scope.withLocalClosure(localClosure) {
            closure.local[ClosureElementA] shouldBe localClosure
        }
    }

    @Test
    fun withLocalClosureShouldRevertLocalClosureProperly() {
        val scope = dispatchScope(LocalClosureContainer(), {}, {})
        scope.withLocalClosure(ClosureElementA()) { }
        scope.closure.local.find(ClosureElementA) shouldBe null
    }
}
