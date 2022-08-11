package com.daftmobile.redukt.core.coroutines

import com.daftmobile.redukt.core.closure.DispatchClosure
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

public class StoreCoroutineScope(
    coroutineScope: CoroutineScope = MainScope(),
) : DispatchClosure.Element, CoroutineScope by coroutineScope {
    override val key: Key = Key

    public companion object Key : DispatchClosure.Key<StoreCoroutineScope>
}