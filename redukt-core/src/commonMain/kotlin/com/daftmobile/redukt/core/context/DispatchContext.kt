package com.daftmobile.redukt.core.context

interface DispatchContext {

    fun <T : Element> find(key: Key<T>): T?

    operator fun <T : Element> get(key: Key<T>): T = find(key) ?: throw MissingContextElementException(key)

    operator fun plus(context: DispatchContext): DispatchContext {
        val incomingElements = context.split()
        val incomingKeys = incomingElements.map(Element::key)
        return CombinedDispatchContext(split().filter { it.key !in incomingKeys } + incomingElements)
    }

    fun split(): List<Element>

    interface Element : DispatchContext {
        val key: Key<*>

        @Suppress("UNCHECKED_CAST")
        override fun <T : Element> find(key: Key<T>): T? = if (this.key == key) this as T else null

        override fun <T : Element> get(key: Key<T>): T = find(key) ?: throw MissingContextElementException(key)

        override fun split(): List<Element> = listOf(this)
    }

    interface Key<T : Element>
}

internal class MissingContextElementException(
    key: DispatchContext.Key<*>
) : IllegalStateException("Element with a key=${key} is not part of a dispatch context!")