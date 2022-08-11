package com.daftmobile.redukt.core.coroutines

import com.daftmobile.redukt.core.context.DispatchContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

public class StoreCoroutineScope(
    coroutineScope: CoroutineScope = MainScope(),
) : DispatchContext.Element, CoroutineScope by coroutineScope {
    override val key: Key = Key

    public companion object Key : DispatchContext.Key<StoreCoroutineScope>
}