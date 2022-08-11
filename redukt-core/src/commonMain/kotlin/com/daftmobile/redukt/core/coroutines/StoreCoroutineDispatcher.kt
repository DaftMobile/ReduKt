package com.daftmobile.redukt.core.coroutines

import com.daftmobile.redukt.core.context.DispatchContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

public class StoreCoroutineDispatcher(
    public val dispatcher: CoroutineDispatcher = Dispatchers.Main,
) : DispatchContext.Element {
    override val key: Key = Key

    public companion object Key : DispatchContext.Key<StoreCoroutineDispatcher>
}