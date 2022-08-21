package com.daftmobile.redukt.core.coroutines

import com.daftmobile.redukt.core.DispatchScope
import com.daftmobile.redukt.core.closure.DispatchClosure
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

public class DispatchCoroutineScope(
    coroutineScope: CoroutineScope,
) : DispatchClosure.Element, CoroutineScope by coroutineScope {
    override val key: Key = Key

    public companion object Key : DispatchClosure.Key<DispatchCoroutineScope>
}

public val DispatchScope<*>.coroutineScope: CoroutineScope get() = closure[DispatchCoroutineScope]
