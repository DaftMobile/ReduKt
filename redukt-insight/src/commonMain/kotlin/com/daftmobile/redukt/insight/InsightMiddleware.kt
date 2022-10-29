package com.daftmobile.redukt.insight

import com.daftmobile.redukt.core.middleware.Middleware
import com.daftmobile.redukt.core.middleware.dispatchFunction

public fun <State> insightMiddleware(
    before: Insight<State> = Insight.empty(),
    after: Insight<State> = Insight.default(),
): Middleware<State> = {
    val beforeInspector = before.toInspector()
    val afterInspector = after.toInspector()
    dispatchFunction { action ->
        val context = InspectionScope(this, action)
        beforeInspector.inspect(context)
        next(action)
        afterInspector.inspect(context)
    }
}

private fun <T> Insight<T>.toInspector(): Inspector<InspectionScope<T>> {
    var context: InspectionScope<T>? = null
    val inspection = inspection { inspect(context!!) }.intercept()
    return Inspector {
        context = it
        inspection.inspect()
    }
}

