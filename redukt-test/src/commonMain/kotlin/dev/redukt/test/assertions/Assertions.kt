package dev.redukt.test.assertions

import dev.redukt.core.Action
import dev.redukt.test.tools.pullOnEach
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * Expects first unverified action to be equal to [action].
 */
public fun ActionsAssertScope.expectActionEquals(action: Action) {
    requiresAtLeasOneAction()
    assertEquals(unverified.pull(), action, "Expected current action to be equal to $action. $stackDescription")
}

/**
 * Expects single unverified action to be equal to [action].
 */
public fun ActionsAssertScope.expectSingleActionEquals(action: Action) {
    requiresAtLeasOneAction()
    assertEquals(unverified.pull(), action, "Expect single action to be equal to $action. $stackDescription")
    requiresSingle()
}

/**
 * Expects first unverified action to be an instance of [T]. Also, it applies additional [checks].
 */
public inline fun <reified T : Action> ActionsAssertScope.expectActionOfType(checks: (T) -> Unit = {}) {
    requiresAtLeasOneAction()
    val action = unverified.pull()
    assertIs<T>(action, "Expected action is not an instance of type ${T::class.simpleName}!").apply(checks)
}

/**
 * Expects first unverified action to be an instance of [T]. Also, it applies additional [checks].
 */
public inline fun <reified T : Action> ActionsAssertScope.expectSingleActionOfType(checks: (T) -> Unit = {}) {
    requiresAtLeasOneAction()
    val action = unverified.pull()
    assertIs<T>(action, "Expected action is not an instance of type ${T::class.simpleName}!").apply(checks)
    requiresSingle()
}

/**
 * Expects every unverified action to match the [predicate].
 */
public fun ActionsAssertScope.expectEvery(message: String? = null, predicate: (Action) -> Boolean) {
    unverified.pullOnEach { action ->
        assertTrue(
            predicate(action),
            (message
                ?: "Expected every action to fulfill specified conditions but $action have not!") + stackDescription
        )
    }
}

/**
 * Expects all dispatched actions count to be equal to [count]. It does not pull any action from unverified queue.
 */
public fun ActionsAssertScope.expectAllActionsCount(count: Int): Unit = assertEquals(
    count,
    history.size,
    "Expected all dispatched actions count to be equal $count!"
)

/**
 * Expects no dispatched actions.
 */
public fun ActionsAssertScope.expectNoActions(): Unit = assertTrue(
    history.isEmpty(),
    "Expected no actions! $stackDescription"
)

/**
 * Expects no unverified actions left.
 */
public fun ActionsAssertScope.expectNoMoreActions() {
    assertTrue(unverified.isEmpty(), "Expected all actions to be processed! $stackDescription")
}

/**
 * Expects only this unverified [actions] and no more.
 */
public inline fun ActionsAssertScope.expectActionsSequence(vararg actions: Action){
    actions.forEach { expectActionEquals(it) }
    expectNoMoreActions()
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