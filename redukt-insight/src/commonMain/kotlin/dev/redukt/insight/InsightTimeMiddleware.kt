package dev.redukt.insight

import dev.redukt.core.middleware.Middleware
import dev.redukt.core.middleware.middleware
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
public fun insightTimeMiddleware(): Middleware<*> = middleware {
    val time = measureTime { next(it) }
    insightContainer[InsightValues.ProcessedActionTime] = time
}