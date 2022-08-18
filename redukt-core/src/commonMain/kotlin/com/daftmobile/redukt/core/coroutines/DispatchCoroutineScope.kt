package com.daftmobile.redukt.core.coroutines

import com.daftmobile.redukt.core.ClosureScope
import com.daftmobile.redukt.core.closure.DispatchClosure
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

public class DispatchCoroutineScope(
    coroutineScope: CoroutineScope = MainScope(),
) : DispatchClosure.Element, CoroutineScope by coroutineScope {
    override val key: Key = Key

    public companion object Key : DispatchClosure.Key<DispatchCoroutineScope>
}

public val ClosureScope.coroutineScope: CoroutineScope get() = closure[DispatchCoroutineScope]