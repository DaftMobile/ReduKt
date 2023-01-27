package com.daftmobile.redukt.test.tools

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.closure.EmptyDispatchClosure
import com.daftmobile.redukt.core.closure.LocalClosureContainer
import com.daftmobile.redukt.core.coroutines.ForegroundJobRegistry
import com.daftmobile.redukt.core.coroutines.SingleForegroundJobRegistry
import com.daftmobile.redukt.test.TestActions.ActionA
import com.daftmobile.redukt.test.TestActions.ActionB
import com.daftmobile.redukt.test.TestActions.ActionC
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.test.Test
import kotlin.test.fail

internal class TestStoreTest {
    private val store = TestStore(0)

    @Test
    fun shouldProvideDispatchedActionsInTestBlockAsUnverified() {
        store.dispatch(ActionA)
        store.dispatch(ActionB)
        store.dispatch(ActionC)
        store.test(strict = false) {
            unverified shouldContainExactly listOf(ActionA, ActionB, ActionC)
        }
    }

    @Test
    fun shouldProvideDispatchedActionsInTestBlockAsHistory() {
        store.dispatch(ActionA)
        store.dispatch(ActionB)
        store.dispatch(ActionC)
        store.test(strict = false) {
            history shouldContainExactly listOf(ActionA, ActionB, ActionC)
        }
    }

    @Test
    fun shouldThrowAssertionErrorWhenStrictTestBlockLeavesUnverifiedActions() {
        store.dispatch(ActionA)
        shouldThrow<AssertionError> {
            store.test(strict = true) { }
        }
    }

    @Test
    fun shouldNotThrowAssertionErrorWhenStrictTestBlockLeavesUnverifiedActions() {
        store.dispatch(ActionA)
        shouldNotThrowAny {
            store.test(strict = false) { }
        }
    }

    @Test
    fun shouldNotThrowAssertionWhenStrictTestVerifiesAllActions() {
        store.dispatch(ActionA)
        shouldNotThrowAny {
            store.test(strict = true) {
                unverified.pull()
            }
        }
    }

    @Test
    fun shouldCallOnDispatch() {
        var calledWithAction: Action? = null
        store.onDispatch { calledWithAction = it }
        store.dispatch(ActionA)
        calledWithAction shouldBe ActionA
    }

    @Test
    fun shouldReplacePreviousOnDispatchWithNextOnDispatchCall() {
        store.onDispatch { fail() }
        store.onDispatch {  }
        store.dispatch(ActionA)
    }

    @Test
    fun shouldProvideNewStateWhenChanged() {
        store.currentState = 1
        store.state.value shouldBe 1
        store.currentState shouldBe 1
    }

    @Test
    fun shouldProvideNewClosureWhenChanged() {
        store.closure = EmptyDispatchClosure
        store.closure shouldBe EmptyDispatchClosure
    }

    @Test
    fun shouldContainTestForegroundJobRegistry() {
        store.closure[ForegroundJobRegistry].shouldBeInstanceOf<TestForegroundJobRegistry>()
    }

    @Test
    fun shouldContainTestLocalClosureContainer() {
        store.closure[LocalClosureContainer].shouldBeInstanceOf<TestLocalClosureContainer>()
    }

    @Test
    fun shouldOverwriteTestForegroundJobRegistryWhenOtherProvided() {
        val closure = SingleForegroundJobRegistry()
        val store = TestStore(0, closure)
        store.closure[ForegroundJobRegistry] shouldBe closure
    }

    @Test
    fun shouldOverwriteTestLocalClosureContainerWhenOtherProvided() {
        val closure = LocalClosureContainer()
        val store = TestStore(0, closure)
        store.closure[LocalClosureContainer] shouldBe closure
    }

    @Test
    fun shouldNotFailWhenDefaultStrictIsFalseAndTestBlockLeavesUnverifiedActions() {
        val store = TestStore(0, strict = false)
        store.dispatch(ActionA)
        shouldNotThrowAny {
            store.test {  }
        }
    }

    @Test
    fun shouldFailWhenDefaultStrictIsTrueAndTestBlockLeavesUnverifiedActions() {
        val store = TestStore(0, strict = true)
        store.dispatch(ActionA)
        shouldThrow<AssertionError> {
            store.test {  }
        }
    }
}