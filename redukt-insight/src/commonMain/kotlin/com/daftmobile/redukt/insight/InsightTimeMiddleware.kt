package com.daftmobile.redukt.insight

import com.daftmobile.redukt.core.middleware.Middleware
import com.daftmobile.redukt.core.middleware.middleware
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
public fun insightTimeMiddleware(): Middleware<*> = middleware {
    val time = measureTime { next(it) }
    insightContainer[InsightValues.ProcessedActionTime] = time
}