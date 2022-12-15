package dev.redukt.test.assertions

import dev.redukt.core.Action

public val ActionsAssertScope.currentAction: Action? get() = unverified.firstOrNull()

public val ActionsAssertScope.actionStackString: String
    get() {
        val processed = (history - unverified)
        val lastProcessed = processed.lastOrNull()
        return (processed + unverified).joinToString(separator = "\t\n") {
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
