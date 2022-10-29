package com.daftmobile.redukt.insight

import com.daftmobile.redukt.core.threading.KtThread
import com.daftmobile.redukt.core.threading.current
import kotlin.time.Duration

public inline fun <T> Inspection<T>.printToSystemOut(): Inspection<T> = onEach { println(it) }

public inline fun <State> Inspection<InspectionScope<State>>.filterByActionTime(
    crossinline predicate: (Duration) -> Boolean
): Inspection<InspectionScope<State>> = filter {
    val processedActionTime = it.insightContainer.processedActionTime
        ?: error("Filtering by action time requires insightTimeMiddleware below insightMiddleware! Read the documentation to make sure your setup is correct!")
    predicate(processedActionTime)
}

public inline fun <State> Inspection<InspectionScope<State>>.mapToActionWithTime(): Inspection<String> = map {
    "[${it.insightContainer.processedActionTime}] ${it.action}"
}

public inline fun <State> Inspection<InspectionScope<State>>.savePreviousState(): Inspection<InspectionScope<State>> = onEach {
    it.insightContainer.previousState = it.currentState
}

public inline fun Inspection<String>.splitByNewLine(): Inspection<String> = map { it.split("\n") }.flatten()

public inline fun Inspection<String>.prependWithThreadName(): Inspection<String> = prependWith("[${KtThread.current().name}] ")

public inline fun Inspection<String>.prependWith(value: String): Inspection<String> = map { "$value$it" }

public inline fun Inspection<String>.appendWith(value: String): Inspection<String> = map { "$it$value" }