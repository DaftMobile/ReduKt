package co.redukt.thunk

import co.redukt.core.Action
import co.redukt.test.assertions.expectActionEquals
import co.redukt.test.assertions.expectNoMoreActions
import co.redukt.test.thunk.testExecute
import kotlin.test.Test
import kotlin.test.assertEquals

internal class MultiDispatchTest {

    private object ActionA : Action
    private object ActionB : Action
    private object ActionC : Action

    private val actionsList = listOf(ActionA, ActionB, ActionC)

    @Test
    fun shouldDispatchPassedActionsWithGivenOrder() = DispatchList(actionsList).testExecute(Unit) {
        expectActionEquals(ActionA)
        expectActionEquals(ActionB)
        expectActionEquals(ActionC)
        expectNoMoreActions()
    }

    @Test
    fun plusOperatorShouldCreateMultipleDispatchFromActions() {
        assertEquals(DispatchList(actionsList), ActionA + ActionB + ActionC)
    }

    @Test
    fun withShouldCreateMultipleDispatchWithPassedSupport() {
        assertEquals(
            DispatchJobSupportList(actionsList, true),
            ActionA + ActionB + ActionC with DispatchJobSupport(concurrent = true)
        )
    }
}
