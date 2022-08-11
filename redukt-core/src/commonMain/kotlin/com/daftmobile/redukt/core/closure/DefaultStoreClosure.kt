package com.daftmobile.redukt.core.closure

import com.daftmobile.redukt.core.coroutines.StoreCoroutineDispatcher
import com.daftmobile.redukt.core.coroutines.StoreCoroutineScope

public fun defaultStoreClosure(): DispatchClosure = StoreCoroutineDispatcher() + StoreCoroutineScope()