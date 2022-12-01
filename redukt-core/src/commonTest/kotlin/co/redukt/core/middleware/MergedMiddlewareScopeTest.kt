package co.redukt.core.middleware

import co.redukt.core.*
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class MergedMiddlewareScopeTest {

    private val closure = ClosureElementA()
    private var dispatchFunction: DispatchFunction = { }
    private var nextDispatchFunction: DispatchFunction = { }
    private val dispatchScope by lazy {
        dispatchScope(
            closure = closure,
            dispatch = dispatchFunction,
            getState = { 1 }
        )
    }
    private val middlewareScope by lazy { MergedMiddlewareScope(dispatchScope, nextDispatchFunction) }

    @Test
    fun shouldDelegateClosureToDispatchScope() {
        middlewareScope.closure shouldBe closure
    }

    @Test
    fun shouldDelegateDispatchToDispatchScope() {
        var delegatedAction: Action? = null
        dispatchFunction = { delegatedAction = it }
        middlewareScope.dispatch(KnownAction.A)
        delegatedAction shouldBe KnownAction.A
    }

    @Test
    fun shouldDelegateStateToDispatchScope() {
        middlewareScope.currentState shouldBe 1
    }

    @Test
    fun shouldDelegateNextToNextDispatchFunction() {
        var delegatedAction: Action? = null
        nextDispatchFunction = { delegatedAction = it }
        middlewareScope.next(KnownAction.B)
        delegatedAction shouldBe KnownAction.B
    }
}