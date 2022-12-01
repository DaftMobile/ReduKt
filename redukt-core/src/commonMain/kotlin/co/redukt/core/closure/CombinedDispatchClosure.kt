package co.redukt.core.closure

internal class CombinedDispatchClosure(
    private val elements: Map<DispatchClosure.Key<*>, DispatchClosure.Element>
) : DispatchClosure {

    init {
        require(elements.isNotEmpty()) { "Combined closure cannot be empty!" }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : DispatchClosure.Element> find(key: DispatchClosure.Key<T>): T? = elements[key] as? T

    override fun minus(key: DispatchClosure.Key<*>): DispatchClosure = CombinedDispatchClosure(elements - key)

    override fun scatter(): Map<DispatchClosure.Key<*>, DispatchClosure.Element> = elements

    override fun toString(): String = elements.toString()
}
