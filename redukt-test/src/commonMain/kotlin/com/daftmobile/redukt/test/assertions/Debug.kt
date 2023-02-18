package com.daftmobile.redukt.test.assertions

import com.daftmobile.redukt.core.Action

/**
 * Returns first unverified action.
 */
public val ActionsAssertScope.currentAction: Action? get() = unverified.firstOrNull()

/**
 * Returns a string that contains list of all dispatched actions.
 * The action recently pulled form the queue is marked with '->'.
 */
public val ActionsAssertScope.actionStackString: String
    get() {
        val lastUnverified = history.size - unverified.size - 1
        return history.mapIndexed { index, action ->
            if (index == lastUnverified) "-> $action" else "   $action"
        }.joinToString(separator = "\t\n")
    }

/**
 * Prints information about dispatched actions based on [actionStackString].
 */
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
