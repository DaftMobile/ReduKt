package com.daftmobile.redukt.test.tools

import com.daftmobile.redukt.core.DelicateReduKtApi
import com.daftmobile.redukt.core.closure.DispatchClosure
import com.daftmobile.redukt.core.closure.EmptyDispatchClosure
import com.daftmobile.redukt.core.closure.LocalClosureContainer
import com.daftmobile.redukt.core.closure.LocalClosureContainer.Frame
import com.daftmobile.redukt.core.closure.LocalClosureContainer.Slot

/**
 * Provides [LocalClosureContainer] that ignores local changes and returns [closure].
 */
@DelicateReduKtApi
public class TestLocalClosureContainer(private val closure: DispatchClosure = EmptyDispatchClosure) : LocalClosureContainer {

    override fun applyTo(closure: DispatchClosure): DispatchClosure = closure + this.closure

    override fun registerNewSlot(closure: DispatchClosure): Slot = Slot()

    override fun removeSlot(slot: Slot): Unit = Unit
    override fun registerNewFrame(): Frame = Frame()

    override fun removeFrame(frame: Frame): Unit = Unit
}