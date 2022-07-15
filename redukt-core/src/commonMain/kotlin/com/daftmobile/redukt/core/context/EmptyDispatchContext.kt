package com.daftmobile.redukt.core.context

public object EmptyDispatchContext : DispatchContext {

    override fun <T : DispatchContext.Element> find(key: DispatchContext.Key<T>): T? = null

    override fun <T : DispatchContext.Element> get(key: DispatchContext.Key<T>): T = throw MissingContextElementException(key)

    override fun plus(context: DispatchContext): DispatchContext = context

    override fun split(): List<DispatchContext.Element> = emptyList()
}