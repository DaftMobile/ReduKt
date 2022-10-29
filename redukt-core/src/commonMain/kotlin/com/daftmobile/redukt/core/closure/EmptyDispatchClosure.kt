package com.daftmobile.redukt.core.closure

public object EmptyDispatchClosure : DispatchClosure {

    override fun <T : DispatchClosure.Element> find(key: DispatchClosure.Key<T>): T? = null

    override fun <T : DispatchClosure.Element> get(key: DispatchClosure.Key<T>): T = throw MissingClosureElementException(key)

    override fun plus(closure: DispatchClosure): DispatchClosure = closure

    override fun split(): Map<DispatchClosure.Key<*>, DispatchClosure.Element> = emptyMap()
}