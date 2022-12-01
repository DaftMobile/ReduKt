package co.redukt.test.tools

import co.redukt.core.DelicateReduKtApi
import co.redukt.core.closure.DispatchClosure
import co.redukt.core.closure.LocalClosure
import co.redukt.core.closure.LocalSlot

@DelicateReduKtApi
public class ImmutableLocalClosure(private val closureProvider: () -> DispatchClosure) : LocalClosure {

    override val current: DispatchClosure get() = closureProvider()

    override fun registerNewSlot(closure: DispatchClosure): LocalSlot = LocalSlot()

    override fun removeSlot(slot: LocalSlot): Unit = Unit
}