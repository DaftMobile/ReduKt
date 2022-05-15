package com.daftmobile.redukt.test.assertions

import com.daftmobile.redukt.core.Action

val ActionsAssertScope.currentAction: Action? get() = pipeline.firstOrNull()

val ActionsAssertScope.actionStackString
    get(): String {
        val processed = (history - pipeline)
        val lastProcessed = processed.lastOrNull()
        return (processed + pipeline).joinToString(separator = "\t\n") {
            if (it == lastProcessed) "-> $it" else "   $it"
        }
    }

fun ActionsAssertScope.printActionsStack() {
    println(stackDescription)
}

@PublishedApi
internal val ActionsAssertScope.stackDescription
    get() = if (history.isEmpty()) {
        "Current stack is empty!"
    } else {
        "Current actions stack: \n${actionStackString}\n"
    }
