package com.daftmobile.redukt.insight

import com.daftmobile.redukt.core.threading.KtThread
import com.daftmobile.redukt.core.threading.current
import com.daftmobile.redukt.insight.InsightValues.PreviousState
import com.daftmobile.redukt.insight.InsightValues.ProcessedActionTime
import kotlin.time.Duration

public inline fun <T> Inspection<T>.printToSystemOut(): Inspection<T> = onEach { println(it) }

public inline fun <State> Inspection<InspectionScope<State>>.filterByActionTime(
    crossinline predicate: (Duration) -> Boolean
): Inspection<InspectionScope<State>> = filter {
    val processedActionTime = it.insightContainer[ProcessedActionTime]
        ?: error("Filtering by action time requires insightTimeMiddleware below insightMiddleware! Read the documentation to make sure your setup is correct!")
    predicate(processedActionTime)
}

public inline fun <State> Inspection<InspectionScope<State>>.mapToActionWithTime(): Inspection<String> = map {
    "[${it.insightContainer[ProcessedActionTime]}] ${it.action}"
}

public inline fun <State> Inspection<InspectionScope<State>>.savePreviousState(): Inspection<InspectionScope<State>> = onEach {
    it.insightContainer[PreviousState] = it.currentState
}

public inline fun Inspection<String>.splitByNewLine(): Inspection<String> = transform { str ->
    str.split("\n").onEach(::inspect)
}

public inline fun Inspection<String>.prependWithThreadName(): Inspection<String> = prependWith("[${KtThread.current().name}] ")

public inline fun Inspection<String>.prependWith(value: String): Inspection<String> = map { "$value$it" }

public inline fun Inspection<String>.appendWith(value: String): Inspection<String> = map { "$it$value" }