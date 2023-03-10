package com.daftmobile.redukt.core.coroutines

import com.daftmobile.redukt.core.DispatchScope
import com.daftmobile.redukt.core.closure.DispatchClosure
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlin.coroutines.CoroutineContext

/**
 * Default [CoroutineScope] associated with a store. By default, the store contains a [MainScope] and it can be replaced
 * at store creation by adding it into a [DispatchClosure]. It can be accessed by [coroutineScope] extension property.
 * Tools provided by this library most likely use this scope to launch a coroutine.
 */
public class DispatchCoroutineScope(
    private val coroutineScope: CoroutineScope,
) : DispatchClosure.Element, CoroutineScope by coroutineScope {
    override val key: Key = Key

    public constructor(context: CoroutineContext) : this(CoroutineScope(context))

    override fun toString(): String = "DispatchCoroutineScope($coroutineScope)"

    public companion object Key : DispatchClosure.Key<DispatchCoroutineScope>
}

/**
 * Returns [CoroutineScope] associated with a store.
 */
public val DispatchScope<*>.coroutineScope: CoroutineScope get() = closure[DispatchCoroutineScope]
