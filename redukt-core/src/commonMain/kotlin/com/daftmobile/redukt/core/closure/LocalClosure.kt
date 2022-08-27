package com.daftmobile.redukt.core.closure

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.DispatchScope
import com.daftmobile.redukt.core.InternalReduKtApi

@InternalReduKtApi
public class LocalClosure(
    private val baseClosureProvider: () -> DispatchClosure
) : DispatchClosure.Element {

    override val key: Key = Key

    private val localSlots = linkedMapOf<LocalSlot, DispatchClosure>()

    private val current: DispatchClosure get() = baseClosureProvider() + (localSlots.entries.lastOrNull()?.value ?: EmptyDispatchClosure)

    public fun registerNewSlot(closure: DispatchClosure): LocalSlot = LocalSlot().also { localSlots[it] = closure }

    public fun removeSlot(slot: LocalSlot) {
        localSlots.remove(slot)
    }

    override fun <T : DispatchClosure.Element> get(key: DispatchClosure.Key<T>): T = current[key]

    override fun <T : DispatchClosure.Element> find(key: DispatchClosure.Key<T>): T? = current.find(key)

    override fun toString(): String = "Local($current)"

    public companion object Key : DispatchClosure.Key<LocalClosure>
}

@InternalReduKtApi
public class LocalSlot

@InternalReduKtApi
public val DispatchScope<*>.localClosure: LocalClosure get() = closure[LocalClosure]

@InternalReduKtApi
public fun <T> DispatchScope<*>.withLocalClosure(closure: DispatchClosure, block: () -> T): T {
    val slot = localClosure.registerNewSlot(closure)
    return block().also { localClosure.removeSlot(slot) }
}

@InternalReduKtApi
public fun DispatchScope<*>.dispatchWithLocalClosure(closure: DispatchClosure, action: Action) {
    withLocalClosure(closure) { dispatch(action) }
}
