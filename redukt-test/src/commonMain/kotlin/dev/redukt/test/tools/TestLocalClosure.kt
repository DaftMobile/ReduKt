package dev.redukt.test.tools

import dev.redukt.core.DelicateReduKtApi
import dev.redukt.core.closure.DispatchClosure
import dev.redukt.core.closure.EmptyDispatchClosure
import dev.redukt.core.closure.LocalClosure
import dev.redukt.core.closure.LocalSlot

/**
 * Provides LocalClosure with [closureProvider] and ignores mutations from [registerNewSlot].
 */
@DelicateReduKtApi
public class TestLocalClosure(private var closureProvider: () -> DispatchClosure = { EmptyDispatchClosure }) : LocalClosure {
    override fun setBaseClosureProvider(provider: () -> DispatchClosure) {
        closureProvider = provider
    }

    override val current: DispatchClosure get() = closureProvider()

    override fun registerNewSlot(closure: DispatchClosure): LocalSlot = LocalSlot()

    override fun removeSlot(slot: LocalSlot): Unit = Unit
}