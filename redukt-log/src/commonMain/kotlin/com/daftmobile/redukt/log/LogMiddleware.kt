package com.daftmobile.redukt.log

import com.daftmobile.redukt.core.middleware.translucentMiddleware

fun <State> logMiddleware(
    filter: LogFilter = LogFilter.all(),
    adapter: LogBusAdapter = LogBusAdapter.systemOut(tag = "ReduKt-Log >> "),
) = translucentMiddleware<State> { action ->
    if (filter.filter(action)) adapter.log(action)
}