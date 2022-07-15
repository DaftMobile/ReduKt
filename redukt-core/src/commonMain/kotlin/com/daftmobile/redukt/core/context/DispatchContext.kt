package com.daftmobile.redukt.core.context

public interface DispatchContext {

    public fun <T : Element> find(key: Key<T>): T?

    public operator fun <T : Element> get(key: Key<T>): T = find(key) ?: throw MissingContextElementException(key)

    public operator fun plus(context: DispatchContext): DispatchContext {
        val incomingElements = context.split()
        val incomingKeys = incomingElements.map(Element::key)
        return CombinedDispatchContext(split().filter { it.key !in incomingKeys } + incomingElements)
    }

    public fun split(): List<Element>

    public interface Element : DispatchContext {
        public val key: Key<*>

        @Suppress("UNCHECKED_CAST")
        override fun <T : Element> find(key: Key<T>): T? = if (this.key == key) this as T else null

        override fun <T : Element> get(key: Key<T>): T = find(key) ?: throw MissingContextElementException(key)

        override fun split(): List<Element> = listOf(this)
    }

    public interface Key<T : Element>
}

internal class MissingContextElementException(
    key: DispatchContext.Key<*>
) : IllegalStateException("Element with a key=${key} is not part of a dispatch context!")