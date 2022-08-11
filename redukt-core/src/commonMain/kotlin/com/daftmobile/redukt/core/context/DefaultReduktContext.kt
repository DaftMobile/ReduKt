package com.daftmobile.redukt.core.context

import com.daftmobile.redukt.core.coroutines.StoreCoroutineDispatcher
import com.daftmobile.redukt.core.coroutines.StoreCoroutineScope

public fun defaultStoreDispatchContext(): DispatchContext = StoreCoroutineDispatcher() + StoreCoroutineScope()