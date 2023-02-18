@file:Suppress("UNNECESSARY_INLINE")
package com.daftmobile.redukt.core.closure

import com.daftmobile.redukt.core.DelicateReduKtApi
import com.daftmobile.redukt.core.DispatchScope
import com.daftmobile.redukt.core.closure.LocalClosureContainer.Frame
import com.daftmobile.redukt.core.closure.LocalClosureContainer.Slot

/**
 * A [DispatchClosure.Element], that handles local changes of a [DispatchClosure].
 *
 * Generally, this mechanism is not required to know in normal usage. However, it might be useful to introduce some new
 * special mechanisms to the store.
 *
 * [LocalClosureContainer] has following properties:
 * * It doesn't affect the closure that is a part of. Therefore, it must be accessed directly by
 * [DispatchClosure.local]` to receive locally changed values. Original closure always remains the same.
 * * It works only for a time of dispatch call. Therefore, accessing it outside of dispatch function returns
 * unchanged values.
 * * It's hierarchical. Nested dispatch calls receive accumulated local closures.
 * * Every change is identified by [Slot].
 *
 * These properties are not only determined by the implementation of this interface, but also by the convention.
 * Therefore, it is important to use this mechanism according to the documentation and attached examples.
 * Any local change should occur through [withLocalClosure]. Manual slots manipulation it's not recommended.
 *
 * ### Interactions with a store
 *
 * By default, store contains an instance of [LocalClosureContainer] (created by *LocalClosureContainer()*).
 * It applies changes to the original closure field, so accessing local closure without local changes, returns values
 * from the original one. Because local changes remains only for a time of dispatch call, accessing them from
 * coroutines launched by a middleware, doesn't work.
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
 *        element = localClosure[ClosureElement] // direct access to local closure in dispatch
 *                                               // properly returns local changes
 *
 *        element = closure[ClosureElement]      // direct access to original closure
 *                                               // returns values from the original closure
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
     * @return [Slot] to undo local change
     */
    public fun registerNewSlot(closure: DispatchClosure): Slot

    /**
     * Undo local change associated with a [slot]
     */
    public fun removeSlot(slot: Slot)

    /**
     * Registers a new frame that creates a new hierarchy of local changes.
     */
    public fun registerNewFrame(): Frame

    /**
     * Removes a frame along with local changes that occurred.
     */
    public fun removeFrame(frame: Frame)

    /**
     * Identifies single local change.
     */
    @DelicateReduKtApi
    public class Slot

    /**
     * Identifies a single hierarchy of local changes.
     */
    @DelicateReduKtApi
    public class Frame

    public companion object Key : DispatchClosure.Key<LocalClosureContainer> {

        /**
         * Returns an instance of [LocalClosureContainer].
         * @see [LocalClosureContainer]
         */
        public operator fun invoke(): LocalClosureContainer = CoreLocalClosureContainer()
    }
}



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
    newFrame: Boolean = false,
    block: DispatchScope<*>.() -> T
): T = this.closure.withLocalClosure(closure, newFrame) { block() }

@DelicateReduKtApi
/**
 * Changes [LocalClosureContainer] with a given [closure] for a time of [block] execution.
 */
public inline fun <T> DispatchClosure.withLocalClosure(
    closure: DispatchClosure,
    newFrame: Boolean = false,
    block: DispatchClosure.() -> T
): T {
    val container = this[LocalClosureContainer]
    val frame = if (newFrame) container.registerNewFrame() else null
    val slot = container.registerNewSlot(closure)
    return try {
        block()
    } finally {
        container.removeSlot(slot)
        frame?.let(container::removeFrame)
    }
}

private typealias FramesCollection = MutableMap<Frame, MutableMap<Slot, DispatchClosure>>

private class CoreLocalClosureContainer : LocalClosureContainer {

    private val frames: FramesCollection = linkedMapOf(Frame() to linkedMapOf())
    private var invalidate = true
    private var current: DispatchClosure = EmptyDispatchClosure

    override fun applyTo(closure: DispatchClosure): DispatchClosure {
        if (invalidate) {
            current = closure + currentSlotAccumulation()
        }
        return current
    }

    override fun registerNewSlot(closure: DispatchClosure): Slot = Slot().also {
        currentFrame[it] = closure
        invalidate = true
    }

    override fun removeSlot(slot: Slot) {
        currentFrame.remove(slot)
        invalidate = true
    }

    override fun registerNewFrame(): Frame = Frame().also {
        frames[it] = linkedMapOf()
        invalidate = true
    }

    override fun removeFrame(frame: Frame) {
        frames.remove(frame)
        invalidate = true
    }

    override fun toString(): String = "LocalClosure(currentLocalChanges=${currentSlotAccumulation()})"

    private fun currentSlotAccumulation() = currentFrame.values.fold(EmptyDispatchClosure, DispatchClosure::plus)

    private inline val currentFrame get() = frames.entries.last().value

    companion object Key : DispatchClosure.Key<LocalClosureContainer>
}
