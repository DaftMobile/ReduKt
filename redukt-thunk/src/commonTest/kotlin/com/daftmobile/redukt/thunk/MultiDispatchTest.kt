package com.daftmobile.redukt.thunk

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.test.assertions.expectActionEquals
import com.daftmobile.redukt.test.assertions.expectNoMoreActions
import com.daftmobile.redukt.test.thunk.runTestExecute
import kotlin.test.Test
import kotlin.test.assertEquals

internal class MultiDispatchTest {

    private object ActionA : Action
    private object ActionB : Action
    private object ActionC : Action

    private val actionsList = listOf(ActionA, ActionB, ActionC)

    @Test
    fun shouldDispatchPassedActionsWithGivenOrder() = MultiDispatch(actionsList).runTestExecute(Unit) {
        expectActionEquals(ActionA)
        expectActionEquals(ActionB)
        expectActionEquals(ActionC)
        expectNoMoreActions()
    }

    @Test
    fun plusOperatorShouldCreateMultipleDispatchFromActions() {
        assertEquals(MultiDispatch(actionsList), ActionA + ActionB + ActionC)
    }


    @Test
    fun concurrentShouldChangeMultipleDispatchConcurrentProperty() {
        assertEquals(MultiDispatch(actionsList, true), ActionA + ActionB + ActionC concurrent true)
    }
}