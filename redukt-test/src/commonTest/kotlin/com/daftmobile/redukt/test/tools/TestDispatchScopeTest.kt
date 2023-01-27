package com.daftmobile.redukt.test.tools

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.closure.EmptyDispatchClosure
import com.daftmobile.redukt.core.closure.LocalClosureContainer
import com.daftmobile.redukt.core.coroutines.ForegroundJobRegistry
import com.daftmobile.redukt.core.coroutines.SingleForegroundJobRegistry
import com.daftmobile.redukt.test.TestActions.ActionA
import com.daftmobile.redukt.test.TestActions.ActionB
import com.daftmobile.redukt.test.TestActions.ActionC
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.test.Test
import kotlin.test.fail

internal class TestDispatchScopeTest {
    private val scope = TestDispatchScope(initialState = 0)


    @Test
    fun shouldPutEveryActionToUnverified() {
        scope.dispatch(ActionA)
        scope.dispatch(ActionB)
        scope.dispatch(ActionC)
        scope.unverified shouldContainExactly listOf(ActionA, ActionB, ActionC)
    }

    @Test
    fun shouldPutEveryActionToHistory() {
        scope.dispatch(ActionA)
        scope.dispatch(ActionB)
        scope.dispatch(ActionC)
        scope.history shouldContainExactly listOf(ActionA, ActionB, ActionC)
    }

    @Test
    fun shouldClearUnverifiedAndHistory() {
        scope.dispatch(ActionA)
        scope.clearActionsHistory()
        scope.unverified.shouldBeEmpty()
        scope.history.shouldBeEmpty()
    }

    @Test
    fun shouldCallOnDispatch() {
        var calledWithAction: Action? = null
        scope.onDispatch { calledWithAction = it }
        scope.dispatch(ActionA)
        calledWithAction shouldBe ActionA
    }

    @Test
    fun shouldReplacePreviousOnDispatchWithNextOnDispatchCall() {
        scope.onDispatch { fail() }
        scope.onDispatch {  }
        scope.dispatch(ActionA)
    }

    @Test
    fun shouldContainTestForegroundJobRegistry() {
        scope.closure[ForegroundJobRegistry].shouldBeInstanceOf<TestForegroundJobRegistry>()
    }

    @Test
    fun shouldContainTestLocalClosureContainer() {
        scope.closure[LocalClosureContainer].shouldBeInstanceOf<TestLocalClosureContainer>()
    }

    @Test
    fun shouldOverwriteTestForegroundJobRegistryWhenOtherProvided() {
        val closure = SingleForegroundJobRegistry()
        val scope = TestDispatchScope(0, closure)
        scope.closure[ForegroundJobRegistry] shouldBe closure
    }

    @Test
    fun shouldOverwriteTestLocalClosureContainerWhenOtherProvided() {
        val closure = LocalClosureContainer()
        val scope = TestDispatchScope(0, closure)
        scope.closure[LocalClosureContainer] shouldBe closure
    }

    @Test
    fun shouldContainNotEmptyClosureOnInit() {
        scope.closure shouldNotBe EmptyDispatchClosure
    }
    @Test
    fun shouldOverwriteClosureWithClosureField() {
        scope.closure = EmptyDispatchClosure
        scope.closure shouldBe EmptyDispatchClosure
    }

    @Test
    fun shouldOverwriteTestWithCurrentStateField() {
        scope.currentState = 2
        scope.currentState shouldBe 2
    }

    @Test
    fun shouldContainEmptyActionCollectionsOnInit() {
        scope.unverified.shouldBeEmpty()
        scope.history.shouldBeEmpty()
    }
}