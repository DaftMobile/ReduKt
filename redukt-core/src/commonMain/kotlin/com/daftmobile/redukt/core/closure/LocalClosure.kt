package com.daftmobile.redukt.core.closure

import com.daftmobile.redukt.core.DispatchScope
import com.daftmobile.redukt.core.InternalReduKtApi

@InternalReduKtApi
@Suppress("UNCHECKED_CAST")
public interface LocalClosure : DispatchClosure.Element {

    override val key: Key get() = Key

    public val current: DispatchClosure

    public fun registerNewSlot(closure: DispatchClosure): LocalSlot

    public fun removeSlot(slot: LocalSlot)

    override fun <T : DispatchClosure.Element> get(key: DispatchClosure.Key<T>): T {
        return if (key == LocalClosure) this as T else current[key]
    }

    override fun <T : DispatchClosure.Element> find(key: DispatchClosure.Key<T>): T? {
        return if (key == LocalClosure) this as T else current.find(key)
    }

    public companion object Key : DispatchClosure.Key<LocalClosure>
}

@InternalReduKtApi
public class CoreLocalClosure(
    private val baseClosureProvider: () -> DispatchClosure
) : LocalClosure {

    private val localSlots = linkedMapOf<LocalSlot, DispatchClosure>()

    override val current: DispatchClosure get() = baseClosureProvider() + (localSlots.entries.lastOrNull()?.value ?: EmptyDispatchClosure)

    override fun registerNewSlot(closure: DispatchClosure): LocalSlot = LocalSlot().also { localSlots[it] = closure }

    override fun removeSlot(slot: LocalSlot) {
        localSlots.remove(slot)
    }

    override fun toString(): String = "Local($current)"

    public companion object Key : DispatchClosure.Key<LocalClosure>
}

@InternalReduKtApi
public class LocalSlot

@InternalReduKtApi
public val DispatchScope<*>.localClosure: LocalClosure get() = closure[LocalClosure]

@InternalReduKtApi
public fun <T> DispatchScope<*>.withLocalClosure(closure: DispatchClosure, block: DispatchScope<*>.() -> T): T {
    val slot = localClosure.registerNewSlot(closure)
    return block().also { localClosure.removeSlot(slot) }
}
