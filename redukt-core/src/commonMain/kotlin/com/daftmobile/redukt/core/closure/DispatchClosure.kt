package com.daftmobile.redukt.core.closure

/**
 * A map of [Element] instances. Every element has a unique [Key]. By design [DispatchClosure] is immutable.
 * However, its elements might mutate depending on their implementation.
 *
 * This concept is analogous to CoroutineContext and acts in the same way.
 *
 * Examples below shows how to combine and access elements:
 * ```
 * val closure = A(1) + B("arg") + A(2)
 * val elementA = closure[A] // Returns A(2). A(1) was replaced.
 * val elementB = closure[B] // Returns B("arg")
 * val elementC = closure.find(C) // Returns null
 * val elementC = closure[C] // Throws exception
 * ```
 *
 * @see Element
 */
public interface DispatchClosure {

    /**
     * Returns [Element] associated with the [key] or null.
     */
    public fun <T : Element> find(key: Key<T>): T?

    /**
     * Returns [Element] associated with the [key] or throws [MissingClosureElementException].
     */
    public operator fun <T : Element> get(key: Key<T>): T = find(key) ?: throw MissingClosureElementException(key)

    /**
     * Combines this closure with incoming [closure].
     * If both closures contain elements with the same keys, the ones from the incoming [closure] remain.
     */
    public operator fun plus(
        closure: DispatchClosure
    ): DispatchClosure = CombinedDispatchClosure(scatter() + closure.scatter())

    /**
     * Returns a closure without the element with a given [key]
     */
    public operator fun minus(key: Key<*>): DispatchClosure

    /**
     * Returns elements form this [DispatchClosure] as a [Map].
     */
    public fun scatter(): Map<Key<*>, Element>


    /**
     * A single element of a [DispatchClosure]. By default, it acts like a [DispatchClosure] that contains only itself.
     */
    public interface Element : DispatchClosure {

        /**
         * [Key] of this element.
         */
        public val key: Key<*>

        @Suppress("UNCHECKED_CAST")
        override fun <T : Element> find(key: Key<T>): T? = if (this.key == key) this as T else null

        override fun <T : Element> get(key: Key<T>): T = find(key) ?: throw MissingClosureElementException(key)

        override fun minus(key: Key<*>): DispatchClosure = if (this.key == key) EmptyDispatchClosure else this

        override fun scatter(): Map<Key<*> ,Element> = mapOf(key to this)
    }

    /**
     * Unique key for a [Element]. Contains [T] type of this element.
     */
    public interface Key<T : Element>
}

public fun <T : DispatchClosure.Element> DispatchClosure.findOrElse(
    key: DispatchClosure.Key<T>,
    value: T
): T = find(key) ?: value

internal class MissingClosureElementException(
    key: DispatchClosure.Key<*>
) : IllegalStateException("Element with a key=${key} is not part of a dispatch closure!")
