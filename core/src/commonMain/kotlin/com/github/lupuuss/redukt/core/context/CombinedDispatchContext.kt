package com.github.lupuuss.redukt.core.context

internal class CombinedDispatchContext(
    private val elements: List<DispatchContext.Element>
) : DispatchContext {

    init {
        require(elements.isNotEmpty()) { "Combined context cannot be empty!" }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : DispatchContext.Element> find(key: DispatchContext.Key<T>): T? = elements.find { it.key == key } as? T

    override fun split(): List<DispatchContext.Element> = elements
}