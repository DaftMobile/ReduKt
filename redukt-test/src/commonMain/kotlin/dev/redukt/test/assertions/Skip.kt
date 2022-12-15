package dev.redukt.test.assertions

import dev.redukt.core.Action
import dev.redukt.test.tools.pullOrNull
import kotlin.test.fail

/**
 * Skips first unverified action.
 */
public fun ActionsAssertScope.skipOneAction(): Unit = skipActions(1)

/**
 * Skips first [n] unverified actions.
 */
public fun ActionsAssertScope.skipActions(n: Int): Unit = repeat(n) {
    unverified.pullOrNull() ?: fail("No action to skip!")
}

/**
 * Skips actions while [predicate] is true.
 */
public fun ActionsAssertScope.skipActionsWhile(predicate: (Action) -> Boolean) {
    while (unverified.firstOrNull()?.let(predicate) == true) unverified.pull()
}

/**
 * Skips all unverified actions.
 */
public fun ActionsAssertScope.skipOtherActions(): Unit = skipActionsWhile { true }