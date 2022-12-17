package dev.redukt.test.assertions

import dev.redukt.core.Action
import dev.redukt.test.tools.pullEach
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * Asserts that first unverified action is equal to [action].
 */
public fun ActionsAssertScope.assertActionEquals(action: Action) {
    requiresAtLeasOneAction()
    assertEquals(unverified.pull(), action, "Expected current action to be equal to $action. $stackDescription")
}

/**
 * Asserts that first unverified action is equal to [action] and it is the only one dispatched action.
 */
public fun ActionsAssertScope.assertSingleActionEquals(action: Action) {
    requiresAtLeasOneAction()
    assertEquals(unverified.pull(), action, "Expect single action to be equal to $action. $stackDescription")
    requiresSingle()
}

/**
 * Asserts that first unverified action is an instance of type [T]. Also, it applies additional [assertions].
 */
public inline fun <reified T : Action> ActionsAssertScope.assertActionOfType(assertions: (T) -> Unit = {}) {
    requiresAtLeasOneAction()
    val action = unverified.pull()
    assertIs<T>(action, "Expected action is not an instance of type ${T::class.simpleName}!").apply(assertions)
}

/**
 * Asserts that first unverified action is an instance of [T] and it is the only one dispatched action.
 * Also, it applies additional [assertions].
 */
public inline fun <reified T : Action> ActionsAssertScope.assertSingleActionOfType(assertions: (T) -> Unit = {}) {
    requiresAtLeasOneAction()
    val action = unverified.pull()
    assertIs<T>(action, "Expected action is not an instance of type ${T::class.simpleName}!").apply(assertions)
    requiresSingle()
}

/**
 * Asserts that every unverified action matches the [predicate].
 */
public fun ActionsAssertScope.assertEveryAction(message: String? = null, predicate: (Action) -> Boolean) {
    unverified.pullEach { action ->
        assertTrue(
            predicate(action),
            (message
                ?: "Expected every action to fulfill specified conditions but $action have not!") + stackDescription
        )
    }
}

/**
 * Asserts that all dispatched actions count is equal to [count]. It does not pull any action from unverified queue.
 */
public fun ActionsAssertScope.assertAllActionsCount(count: Int): Unit = assertEquals(
    count,
    history.size,
    "Expected all dispatched actions count to be equal $count!"
)

/**
 * Asserts that there is no dispatched actions.
 */
public fun ActionsAssertScope.assertNoActions(): Unit = assertTrue(
    history.isEmpty(),
    "Expected no actions! $stackDescription"
)

/**
 * Asserts that there is no unverified actions left.
 */
public fun ActionsAssertScope.assertNoMoreActions() {
    assertTrue(unverified.isEmpty(), "Expected all actions to be processed! $stackDescription")
}

/**
 * Asserts that there are only [actions] dispatched in given order and no more.
 */
public inline fun ActionsAssertScope.assertActionSequence(vararg actions: Action) {
    actions.forEach { assertActionEquals(it) }
    assertNoMoreActions()
}

@PublishedApi
internal fun ActionsAssertScope.requiresAtLeasOneAction() {
    assertTrue(
        unverified.isNotEmpty(),
        "This assertion requires at least one action to process, but no actions left to process! $stackDescription",
    )
}

@PublishedApi
internal fun ActionsAssertScope.requiresSingle() {
    assertTrue(
        unverified.isEmpty(),
        "Expected single action, but more actions have been dispatched! $stackDescription"
    )
}