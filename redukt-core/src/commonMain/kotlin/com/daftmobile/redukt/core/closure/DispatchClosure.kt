package com.daftmobile.redukt.core.closure

public interface DispatchClosure {

    public fun <T : Element> find(key: Key<T>): T?

    public operator fun <T : Element> get(key: Key<T>): T = find(key) ?: throw MissingClosureElementException(key)

    public operator fun plus(closure: DispatchClosure): DispatchClosure {
        val incomingElements = closure.split()
        val incomingKeys = incomingElements.map(Element::key)
        return CombinedDispatchClosure(split().filter { it.key !in incomingKeys } + incomingElements)
    }

    public fun split(): List<Element>

    public interface Element : DispatchClosure {
        public val key: Key<*>

        @Suppress("UNCHECKED_CAST")
        override fun <T : Element> find(key: Key<T>): T? = if (this.key == key) this as T else null

        override fun <T : Element> get(key: Key<T>): T = find(key) ?: throw MissingClosureElementException(key)

        override fun split(): List<Element> = listOf(this)
    }

    public interface Key<T : Element>
}

internal class MissingClosureElementException(
    key: DispatchClosure.Key<*>
) : IllegalStateException("Element with a key=${key} is not part of a dispatch closure!")