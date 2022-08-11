package com.daftmobile.redukt.core.closure

internal class CombinedDispatchClosure(private val elements: List<DispatchClosure.Element>) : DispatchClosure {

    init {
        require(elements.isNotEmpty()) { "Combined closure cannot be empty!" }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : DispatchClosure.Element> find(key: DispatchClosure.Key<T>): T? = elements.find { it.key == key } as? T

    override fun split(): List<DispatchClosure.Element> = elements
}