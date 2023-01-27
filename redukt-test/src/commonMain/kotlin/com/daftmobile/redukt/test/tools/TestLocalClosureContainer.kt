package com.daftmobile.redukt.test.tools

import com.daftmobile.redukt.core.DelicateReduKtApi
import com.daftmobile.redukt.core.closure.DispatchClosure
import com.daftmobile.redukt.core.closure.EmptyDispatchClosure
import com.daftmobile.redukt.core.closure.LocalClosureContainer
import com.daftmobile.redukt.core.closure.LocalSlot

/**
 * Provides [LocalClosureContainer] that ignores local changes and returns [closure].
 */
@DelicateReduKtApi
public class TestLocalClosureContainer(private val closure: DispatchClosure = EmptyDispatchClosure) : LocalClosureContainer {

    override fun applyTo(closure: DispatchClosure): DispatchClosure = closure + this.closure

    override fun registerNewSlot(closure: DispatchClosure): LocalSlot = LocalSlot()

    override fun removeSlot(slot: LocalSlot): Unit = Unit
}