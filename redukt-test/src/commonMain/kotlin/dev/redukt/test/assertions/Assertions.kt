package dev.redukt.test.assertions

import dev.redukt.core.Action
import dev.redukt.test.tools.pullOrNull
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

public fun ActionsAssertScope.expectActionEquals(action: Action) {
    requiresAtLeasOneAction()
    assertEquals(unverified.pull(), action, "Expect current action to be equal to $action. $stackDescription")
}

public inline fun <reified T : Action> ActionsAssertScope.expectActionOfType(checks: (T) -> Unit = {}) {
    requiresAtLeasOneAction()
    val action = unverified.pull()
    assertIs<T>(action, "Expected action is not an instance of type ${T::class.simpleName}!").apply(checks)
}

public inline fun <reified T : Action> ActionsAssertScope.expectSingleActionOfType(checks: (T) -> Unit = {}) {
    requiresAtLeasOneAction()
    val action = unverified.pull()
    assertIs<T>(action, "Expected action is not an instance of type ${T::class.simpleName}!").apply(checks)
    requiresSingle()
}

public fun ActionsAssertScope.expectSingleActionEquals(action: Action) {
    requiresAtLeasOneAction()
    assertEquals(unverified.pull(), action, "Expect single action to be equal to $action. $stackDescription")
    requiresSingle()
}

public fun ActionsAssertScope.expectEvery(message: String? = null, actionCheck: (Action) -> Boolean) {
    while (true) {
        val action = unverified.pullOrNull() ?: return
        assertTrue(
            actionCheck(action),
            (message
                ?: "Expected every action to fulfill specified conditions but $action have not!") + stackDescription
        )
    }
}

public fun ActionsAssertScope.expectAllActionsCount(count: Int): Unit = assertEquals(
    count,
    history.size,
    "Expected all dispatched actions count to be equal $count!"
)

public fun ActionsAssertScope.expectNoActions(): Unit = assertTrue(
    history.isEmpty(),
    "Expected no actions! $stackDescription"
)

public fun ActionsAssertScope.expectNoMoreActions() {
    assertTrue(unverified.isEmpty(), "Expected all actions to be processed! $stackDescription")
}

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