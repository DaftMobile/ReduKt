package com.daftmobile.redukt.core.coroutines

import com.daftmobile.redukt.core.closure.DispatchClosure
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

public class StoreCoroutineDispatcher(
    public val dispatcher: CoroutineDispatcher = Dispatchers.Main,
) : DispatchClosure.Element {
    override val key: Key = Key

    public companion object Key : DispatchClosure.Key<StoreCoroutineDispatcher>
}