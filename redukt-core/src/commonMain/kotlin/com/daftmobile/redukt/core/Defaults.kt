package com.daftmobile.redukt.core

import com.daftmobile.redukt.core.closure.DispatchClosure
import com.daftmobile.redukt.core.coroutines.StoreCoroutineScope

public fun defaultStoreClosure(): DispatchClosure = StoreCoroutineScope()