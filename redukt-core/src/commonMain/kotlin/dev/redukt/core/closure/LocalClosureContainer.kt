@file:Suppress("UNNECESSARY_INLINE")
package dev.redukt.core.closure

import dev.redukt.core.DispatchScope
import dev.redukt.core.DelicateReduKtApi

/**
 * A [DispatchClosure.Element], that handles local changes of a [DispatchClosure].
 *
 * Generally, this mechanism is not required to know in normal usage. However, it might be useful to introduce some new special mechanisms to the store.
 *
 * [LocalClosureContainer] has following properties:
 * * It doesn't affect the closure that is a part of. Therefore, it must be accessed directly by [DispatchClosure.local]` to receive locally changed values. Original closure always remains the same.
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
 * By default, store contains an instance of [LocalClosureContainer] (created by *LocalClosureContainer()*).
 * It applies changes to the original closure field, so accessing local closure without local changes, returns values from the original one.
 * Because local changes remains only for a time of dispatch call, accessing them from coroutines launched by a middleware, don't work.
 *
 * ### Example
 *
 * How to perform [DispatchClosure] change:
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
public interface LocalClosureContainer : DispatchClosure.Element {

    override val key: Key get() = Key

    /**
     * Returns [closure] with local changes applied.
     */
    public fun applyTo(closure: DispatchClosure): DispatchClosure

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

    public companion object Key : DispatchClosure.Key<LocalClosureContainer> {

        /**
         * Returns an instance of [LocalClosureContainer].
         * @see [LocalClosureContainer]
         */
        public operator fun invoke(): LocalClosureContainer = CoreLocalClosureContainer()
    }
}

/**
 * Identifies single local change.
 */
@DelicateReduKtApi
public class LocalSlot

/**
 * Returns locally changed [DispatchClosure] from this scope.
 */
@DelicateReduKtApi
public inline val DispatchScope<*>.localClosure: DispatchClosure get() = closure.local

/**
 * Returns this closure with local changes.
 */
@DelicateReduKtApi
public inline val DispatchClosure.local: DispatchClosure get() = this[LocalClosureContainer].applyTo(this)

/**
 * Adds local changes to [LocalClosureContainer] with a given [closure] for a time of [block] execution.
 */
@DelicateReduKtApi
public inline fun <T> DispatchScope<*>.withLocalClosure(
    closure: DispatchClosure,
    block: DispatchScope<*>.() -> T
): T = this.closure.withLocalClosure(closure) { block() }

@DelicateReduKtApi
/**
 * Changes [LocalClosureContainer] with a given [closure] for a time of [block] execution.
 */
public inline fun <T> DispatchClosure.withLocalClosure(closure: DispatchClosure, block: DispatchClosure.() -> T): T {
    val slot = this[LocalClosureContainer].registerNewSlot(closure)
    return block().also { this[LocalClosureContainer].removeSlot(slot) }
}

private class CoreLocalClosureContainer : LocalClosureContainer {

    private val localSlots = linkedMapOf<LocalSlot, DispatchClosure>()
    private var invalidate = true
    private var current: DispatchClosure = EmptyDispatchClosure

    override fun applyTo(closure: DispatchClosure): DispatchClosure {
        if (invalidate) {
            current = closure + currentSlotAccumulation()
        }
        return current
    }


    override fun registerNewSlot(closure: DispatchClosure): LocalSlot = LocalSlot().also {
        localSlots[it] = closure
        invalidate = true
    }

    override fun removeSlot(slot: LocalSlot) {
        localSlots.remove(slot)
        invalidate = true
    }

    override fun toString(): String = "LocalClosure(currentLocalChanges=${currentSlotAccumulation()})"

    private fun currentSlotAccumulation() = localSlots.values.fold(EmptyDispatchClosure, DispatchClosure::plus)

    companion object Key : DispatchClosure.Key<LocalClosureContainer>
}