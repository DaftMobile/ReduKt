package dev.redukt.thunk.utils

import dev.redukt.core.coroutines.ForegroundJobAction
import dev.redukt.test.assertions.assertActionSequence
import dev.redukt.test.thunk.tester
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class JoiningCoroutinesDispatchListTest {

    private object ActionA : ForegroundJobAction
    private object ActionB : ForegroundJobAction
    private object ActionC : ForegroundJobAction

    private val actionsList = listOf(ActionA, ActionB, ActionC)

    private val tester = JoiningCoroutinesDispatchList(actionsList, true).tester(Unit)
    @Test
    fun shouldDispatchPassedActionsWithGivenOrder() = runTest {
        tester.test {
            testExecute()
            assertActionSequence(ActionA, ActionB, ActionC)
        }
    }
}