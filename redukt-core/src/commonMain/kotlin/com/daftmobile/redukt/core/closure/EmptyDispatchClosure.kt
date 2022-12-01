package com.daftmobile.redukt.core.closure

/**
 * An empty [DispatchClosure].
 */
public object EmptyDispatchClosure : DispatchClosure {

    override fun <T : DispatchClosure.Element> find(key: DispatchClosure.Key<T>): Nothing? = null

    override fun <T : DispatchClosure.Element> get(key: DispatchClosure.Key<T>): Nothing =
        throw MissingClosureElementException(key)

    override fun plus(closure: DispatchClosure): DispatchClosure = closure

    override fun minus(key: DispatchClosure.Key<*>): DispatchClosure = this

    override fun scatter(): Map<DispatchClosure.Key<*>, DispatchClosure.Element> = emptyMap()
}