package com.daftmobile.redukt.log

import com.daftmobile.redukt.core.Middleware
import com.daftmobile.redukt.core.middleware.translucentMiddleware

public fun <State> logMiddleware(
    filter: LogFilter = LogFilter.all(),
    adapter: LogBusAdapter = LogBusAdapter.systemOut(tag = "ReduKt-Log >> "),
): Middleware<State> = translucentMiddleware { action ->
    if (filter.filter(action)) adapter.log(action)
}