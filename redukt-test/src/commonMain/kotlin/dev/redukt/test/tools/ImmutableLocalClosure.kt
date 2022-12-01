package dev.redukt.test.tools

import dev.redukt.core.DelicateReduKtApi
import dev.redukt.core.closure.DispatchClosure
import dev.redukt.core.closure.LocalClosure
import dev.redukt.core.closure.LocalSlot

@DelicateReduKtApi
public class ImmutableLocalClosure(private val closureProvider: () -> DispatchClosure) : LocalClosure {

    override val current: DispatchClosure get() = closureProvider()

    override fun registerNewSlot(closure: DispatchClosure): LocalSlot = LocalSlot()

    override fun removeSlot(slot: LocalSlot): Unit = Unit
}