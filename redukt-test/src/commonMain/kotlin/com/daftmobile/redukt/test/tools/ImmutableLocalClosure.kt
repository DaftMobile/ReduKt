package com.daftmobile.redukt.test.tools

import com.daftmobile.redukt.core.InternalReduKtApi
import com.daftmobile.redukt.core.closure.DispatchClosure
import com.daftmobile.redukt.core.closure.LocalClosure
import com.daftmobile.redukt.core.closure.LocalSlot

@InternalReduKtApi
public class ImmutableLocalClosure(private val closureProvider: () -> DispatchClosure) : LocalClosure {

    override val current: DispatchClosure get() = closureProvider()

    override fun registerNewSlot(closure: DispatchClosure): LocalSlot = LocalSlot()

    override fun removeSlot(slot: LocalSlot): Unit = Unit
}