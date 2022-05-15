package com.daftmobile.redukt.core.middleware

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.KnownAction
import com.daftmobile.redukt.core.UnknownAction
import com.daftmobile.redukt.test.assertions.expectActionEquals
import com.daftmobile.redukt.test.assertions.expectNoMoreActions
import com.daftmobile.redukt.test.assertions.skipOneAction
import com.daftmobile.redukt.test.middleware.tester
import kotlin.test.Test

internal class MiddlewareClosureTest {

    private object InitAction : Action
    private data class InternalStateAction(val value: Int) : Action

    private val middleware = middlewareClosure {
        dispatch(InitAction)
        var internalState = 0
        consumingMiddleware<Unit, KnownAction> {
            dispatch(InternalStateAction(internalState++))
        }
    }

    private val tester = middleware.tester(Unit)

    @Test
    fun shouldDispatchInitActionOnAnyFirstAction() = tester.test {
        onAction(UnknownAction)
        expectActionEquals(InitAction)
        expectNoMoreActions()
    }

    @Test
    fun shouldProperlyPassActionToWrappedMiddleware() = tester.test {
        onAction(KnownAction.A)
        skipOneAction()
        expectActionEquals(InternalStateAction(0))
        expectNoMoreActions()
    }

    @Test
    fun shouldAllowWrappedMiddlewareToUseClosureVariablesProperly() = tester.test {
        onAllActions(KnownAction.A, KnownAction.A, KnownAction.A)
        skipOneAction()
        expectActionEquals(InternalStateAction(0))
        expectActionEquals(InternalStateAction(1))
        expectActionEquals(InternalStateAction(2))
        expectNoMoreActions()
    }
}