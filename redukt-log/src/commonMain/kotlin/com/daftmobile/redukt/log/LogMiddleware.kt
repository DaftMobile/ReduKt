package com.daftmobile.redukt.log

import com.daftmobile.redukt.core.middleware.Middleware
import com.daftmobile.redukt.core.middleware.translucentMiddleware

public fun logMiddleware(
    filter: LogFilter = LogFilter.all(),
    adapter: LogBusAdapter = LogBusAdapter.systemOut(tag = "ReduKt-Log >> "),
): Middleware<*> = translucentMiddleware { action ->
    if (filter.filter(action)) adapter.log(action)
}