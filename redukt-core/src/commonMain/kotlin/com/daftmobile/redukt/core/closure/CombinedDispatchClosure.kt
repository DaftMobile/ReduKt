package com.daftmobile.redukt.core.closure

internal fun combinedDispatchClosureOf(
    elements: Map<DispatchClosure.Key<*>, DispatchClosure.Element>
): DispatchClosure =  when (elements.values.size) {
    0 -> EmptyDispatchClosure
    1 -> elements.values.single()
    else -> CombinedDispatchClosure(elements)
}

private class CombinedDispatchClosure(
    private val elements: Map<DispatchClosure.Key<*>, DispatchClosure.Element>
) : DispatchClosure {

    init {
        require(elements.isNotEmpty()) { "Combined closure cannot be empty!" }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : DispatchClosure.Element> find(key: DispatchClosure.Key<T>): T? = elements[key] as? T

    override fun minus(key: DispatchClosure.Key<*>): DispatchClosure = combinedDispatchClosureOf(elements - key)

    override fun scatter(): Map<DispatchClosure.Key<*>, DispatchClosure.Element> = elements

    override fun toString(): String = elements.values.joinToString(separator = " + ")
}
