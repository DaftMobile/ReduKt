package com.daftmobile.redukt.log

import com.daftmobile.redukt.core.middleware.Middleware
import com.daftmobile.redukt.core.middleware.middleware

public fun interface LogPipeline<in State> {

    public fun intercept(logger: Logger<LogContext<State>>): Logger<Any?>

    public companion object {
        public fun default(tag: String = "ReduKt-Log >> "): LogPipeline<Any?> = LogPipeline {
            it.selectAction()
                .mapToString()
                .prependWithThreadName()
                .prependWith(tag)
                .printToSystemOut()
        }

        public fun empty(): LogPipeline<Any?> = LogPipeline { it }
    }
}

public fun <State> logMiddleware(
    before: LogPipeline<State> = LogPipeline.default(),
    after: LogPipeline<State> = LogPipeline.empty(),
): Middleware<State> = middleware { action ->
    val context = LogContext(this, action)
    val logger = logger { log(context) }
    before.intercept(logger)
    next(action)
    after.intercept(logger)

}