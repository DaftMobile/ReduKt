package dev.redukt.test.tools

import dev.redukt.core.DelicateReduKtApi
import dev.redukt.core.closure.DispatchClosure
import dev.redukt.core.closure.EmptyDispatchClosure
import dev.redukt.core.closure.LocalClosureContainer
import dev.redukt.core.closure.LocalSlot

/**
 * Provides [LocalClosureContainer] that ignores local changes and returns [closure].
 */
@DelicateReduKtApi
public class TestLocalClosureContainer(private val closure: DispatchClosure = EmptyDispatchClosure) : LocalClosureContainer {

    override fun applyTo(closure: DispatchClosure): DispatchClosure = closure + this.closure

    override fun registerNewSlot(closure: DispatchClosure): LocalSlot = LocalSlot()

    override fun removeSlot(slot: LocalSlot): Unit = Unit
}