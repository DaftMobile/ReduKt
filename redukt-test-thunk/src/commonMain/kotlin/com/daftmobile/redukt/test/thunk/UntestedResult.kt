package com.daftmobile.redukt.test.thunk

class UntestedResultException(result: Result<*>): Exception("Result of executed thunk was not processed! Result: $result")

fun ThunkTesterScope<*>.ignoreReturnValue() {
    runCatching { getReturn() }
        .takeIf { it.exceptionOrNull() != null }
        ?.let { throw UntestedResultException(it) }
}

internal fun ThunkTesterScope<*>.handleUnprocessedResult() {
    if (returnProcessed) return
    runCatching { getReturn() }
        .takeUnless { it.getOrNull() == Unit }
        ?.let { throw UntestedResultException(it) }
}