package dev.redukt.insight

import dev.redukt.core.middleware.Middleware
import dev.redukt.core.middleware.dispatchFunction

public fun <State> insightMiddleware(
    before: Insight<State> = Insight.empty(),
    after: Insight<State> = Insight.debug(),
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
