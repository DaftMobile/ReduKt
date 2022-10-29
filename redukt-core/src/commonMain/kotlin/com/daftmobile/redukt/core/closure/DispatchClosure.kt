package com.daftmobile.redukt.core.closure

public interface DispatchClosure {

    public fun <T : Element> find(key: Key<T>): T?

    public operator fun <T : Element> get(key: Key<T>): T = find(key) ?: throw MissingClosureElementException(key)

    public operator fun plus(closure: DispatchClosure): DispatchClosure = CombinedDispatchClosure(scatter() + closure.scatter())

    public fun scatter(): Map<Key<*>, Element>

    public interface Element : DispatchClosure {
        public val key: Key<*>

        @Suppress("UNCHECKED_CAST")
        override fun <T : Element> find(key: Key<T>): T? = if (this.key == key) this as T else null

        override fun <T : Element> get(key: Key<T>): T = find(key) ?: throw MissingClosureElementException(key)

        override fun scatter(): Map<Key<*> ,Element> = mapOf(key to this)
    }

    public interface Key<T : Element>
}

public fun <T : DispatchClosure.Element> DispatchClosure.findOrElse(key: DispatchClosure.Key<T>, value: T): T = find(key) ?: value

internal class MissingClosureElementException(
    key: DispatchClosure.Key<*>
) : IllegalStateException("Element with a key=${key} is not part of a dispatch closure!")
