package dev.redukt.core.closure

import dev.redukt.core.DispatchScope
import dev.redukt.core.DelicateReduKtApi

/**
 * A [DispatchClosure.Element], which is a [DispatchClosure] that might be changed locally.
 *
 * Generally, this mechanism is not required to know in normal usage. However, it might be useful to introduce some new special mechanisms to the store.
 *
 * [LocalClosure] has following properties:
 * * It doesn't affect the closure that is a part of. Therefore, it must be accessed directly by closure\[LocalClosure] to receive locally changed values. Original closure always remains the same.
 * * Because it is a separate [DispatchClosure.Element], it must receive *original* closure from the outside to actually act like the *original* closure.
 * * It works only for a time of dispatch call. Therefore, accessing it outside of dispatch function returns unchanged values.
 * * It's hierarchical. Nested dispatch calls receive accumulated local closures.
 * * Every change is identified by [LocalSlot].
 *
 * These properties are not only determined by the implementation of this interface, but also by the convention.
 * Therefore, it is important to use this mechanism according to the documentation and attached examples.
 * Any local change should occur through [withLocalClosure]. Manual slots manipulation it's not recommended.
 *
 * ### Interactions with a store
 *
 * By default, store contains an instance of [LocalClosure] (created by *LocalClosure()*).
 * It refers to the original closure field, so accessing local closure without local changes, returns values from the original one.
 * Because local changes remains only for a time of dispatch call, accessing them from coroutines launched by a middleware, don't work.
 *
 * ### Example
 *
 * How to perform [LocalClosure] change:
 * ```
 * fun DispatchScope<*>.customDispatch(action: Action) {
 *    withLocalClosure(ClosureElement()) {
 *        dispatch(SomeAction)
 *    }
 * }
 * ```
 *
 * How to access changed values:
 * ```
 * fun customMiddleware(): Middleware<AppState> = {
 *    var element = localClosure[ClosureElement] // outside of dispatch - returns values from the original closure
 *    dispatchFunction {
 *        element = localClosure[ClosureElement] // inside of dispatch and direct access to local closure - properly returns local changes
 *        element = closure[ClosureElement]      // direct access to original closure - returns values from the original closure
 *    }
 * }
 * ```
 */
@DelicateReduKtApi
@Suppress("UNCHECKED_CAST")
public interface LocalClosure : DispatchClosure.Element {

    override val key: Key get() = Key

    /**
     * Returns current [DispatchClosure] with respect of local changes.
     */
    public val current: DispatchClosure

    /**
     * Registers local change of a closure.
     *
     * @param closure to apply local changes.
     * @return [LocalSlot] to undo local change
     */
    public fun registerNewSlot(closure: DispatchClosure): LocalSlot

    /**
     * Undo local change associated with a [slot]
     */
    public fun removeSlot(slot: LocalSlot)

    /**
     * Returns elements from [current] or throw. If [key] is equal to [Key], returns itself.
     */
    override fun <T : DispatchClosure.Element> get(key: DispatchClosure.Key<T>): T {
        return if (key == LocalClosure) this as T else current[key]
    }

    /**
     * Returns elements from [current] or null. If [key] is equal to [Key], returns itself.
     */
    override fun <T : DispatchClosure.Element> find(key: DispatchClosure.Key<T>): T? {
        return if (key == LocalClosure) this as T else current.find(key)
    }

    public companion object Key : DispatchClosure.Key<LocalClosure>
}

/**
 * Returns an instance of [LocalClosure] that refers to [baseClosureProvider] as an original closure.
 * @see [LocalClosure]
 */
@DelicateReduKtApi
public fun LocalClosure(baseClosureProvider: () -> DispatchClosure): LocalClosure = CoreLocalClosure(baseClosureProvider)

/**
 * Identifies single local change.
 */
@DelicateReduKtApi
public class LocalSlot

/**
 * Returns [LocalClosure] from this scope.
 */
@DelicateReduKtApi
public val DispatchScope<*>.localClosure: LocalClosure get() = closure[LocalClosure]

/**
 * Changes [LocalClosure] with a given [closure] for a time of [block] execution.
 */
@DelicateReduKtApi
public fun <T> DispatchScope<*>.withLocalClosure(closure: DispatchClosure, block: DispatchScope<*>.() -> T): T {
    val slot = localClosure.registerNewSlot(closure)
    return block().also { localClosure.removeSlot(slot) }
}

private class CoreLocalClosure(
    private val baseClosureProvider: () -> DispatchClosure
) : LocalClosure {

    private val localSlots = linkedMapOf<LocalSlot, DispatchClosure>()
    private var _current: DispatchClosure? = null
    override val current: DispatchClosure get() = _current ?: baseClosureProvider()

    override fun registerNewSlot(closure: DispatchClosure): LocalSlot = LocalSlot().also {
        localSlots[it] = closure
        reloadCurrent()
    }

    override fun removeSlot(slot: LocalSlot) {
        localSlots.remove(slot)
        reloadCurrent()
    }

    override fun toString(): String = "Local(${current - LocalClosure})"

    private fun reloadCurrent() {
        _current = baseClosureProvider() + localSlots.values.fold(EmptyDispatchClosure, DispatchClosure::plus)
    }

    companion object Key : DispatchClosure.Key<LocalClosure>
}