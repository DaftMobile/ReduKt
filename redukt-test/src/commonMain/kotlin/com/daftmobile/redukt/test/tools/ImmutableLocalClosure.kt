package com.daftmobile.redukt.test.tools

import com.daftmobile.redukt.core.InternalReduKtApi
import com.daftmobile.redukt.core.closure.DispatchClosure
import com.daftmobile.redukt.core.closure.LocalClosure
import com.daftmobile.redukt.core.closure.LocalSlot

@InternalReduKtApi
public class ImmutableLocalClosure(override val current: DispatchClosure) : LocalClosure {

    override fun registerNewSlot(closure: DispatchClosure): LocalSlot = LocalSlot()

    override fun removeSlot(slot: LocalSlot): Unit = Unit
}