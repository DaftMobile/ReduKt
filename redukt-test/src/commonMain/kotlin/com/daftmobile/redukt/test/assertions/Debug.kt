package com.daftmobile.redukt.test.assertions

import com.daftmobile.redukt.core.Action

public val ActionsAssertScope.currentAction: Action? get() = pipeline.firstOrNull()

public val ActionsAssertScope.actionStackString: String
    get() {
        val processed = (history - pipeline)
        val lastProcessed = processed.lastOrNull()
        return (processed + pipeline).joinToString(separator = "\t\n") {
            if (it == lastProcessed) "-> $it" else "   $it"
        }
    }

public fun ActionsAssertScope.printActionsStack() {
    println(stackDescription)
}

@PublishedApi
internal val ActionsAssertScope.stackDescription: String
    get() = if (history.isEmpty()) {
        "Current stack is empty!"
    } else {
        "Current actions stack: \n${actionStackString}\n"
    }
