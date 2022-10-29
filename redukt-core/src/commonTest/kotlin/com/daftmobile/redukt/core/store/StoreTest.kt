package com.daftmobile.redukt.core.store

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.KnownAction
import com.daftmobile.redukt.core.TestDispatchClosure
import com.daftmobile.redukt.core.closure.DispatchClosure
import com.daftmobile.redukt.core.closure.EmptyDispatchClosure
import com.daftmobile.redukt.core.closure.LocalClosure
import com.daftmobile.redukt.core.coroutines.EmptyForegroundJobRegistry
import com.daftmobile.redukt.core.coroutines.ForegroundJobRegistry
import com.daftmobile.redukt.core.coroutines.SingleForegroundJobRegistry
import com.daftmobile.redukt.core.middleware.Middleware
import com.daftmobile.redukt.core.middleware.dispatchFunction
import com.daftmobile.redukt.core.middleware.middleware
import com.daftmobile.redukt.test.tools.ImmutableLocalClosure
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.test.Test

class StoreTest {

    private val mockReducer = MockReducer<Int>(onReducerCall = { _, _ -> 2 })
    private var middlewares = listOf<Middleware<Int>>()
    private var initialClosure: DispatchClosure = EmptyDispatchClosure
    private val store by lazy { Store(1, mockReducer::call, middlewares, initialClosure) }

    @Test
    fun shouldHaveProperInitialState() {
        store.state.value shouldBe 1
    }

    @Test
    fun shouldHaveEmptyForegroundRegistryInClosure() {
        store.closure.find(ForegroundJobRegistry).shouldBeInstanceOf<EmptyForegroundJobRegistry>()
    }

    @Test
    fun shouldAddInitialClosureToInternalClosure() {
        val testClosure = TestDispatchClosure()
        initialClosure = testClosure
        store.closure.find(TestDispatchClosure) shouldBe testClosure
    }

    @Test
    fun shouldNotOverwriteInternalClosureWithoutClientIntention() {
        initialClosure = TestDispatchClosure()
        storeShouldContainOriginalClosure()
    }

    @Test
    fun shouldOverwriteForegroundJobRegistryWhenInitialClosureContainsCorrectReplacement() {
        val replacedRegistry = SingleForegroundJobRegistry()
        initialClosure = replacedRegistry
        store.closure.find(ForegroundJobRegistry) shouldBe replacedRegistry
        store.closure.find(LocalClosure).shouldNotBeNull()
    }

    @Test
    fun shouldOverwriteLocalClosureWhenInitialClosureContainsCorrectReplacement() {
        val replacedLocal = ImmutableLocalClosure { EmptyDispatchClosure }
        initialClosure = replacedLocal
        store.closure.find(LocalClosure) shouldBe replacedLocal
        store.closure.find(ForegroundJobRegistry).shouldBeInstanceOf<EmptyForegroundJobRegistry>()
    }

    @Test
    fun shouldThrowUninitializedDispatchExceptionWhenActionDispatchedInMiddlewareCreator() {
        val unsafeInitMiddleware: Middleware<Int> = {
            dispatch(KnownAction.A)
            dispatchFunction { }
        }
        middlewares = listOf(unsafeInitMiddleware)
        shouldThrow<UninitializedDispatchException> { store }
    }

    @Test
    fun shouldThrowUnsafeStateAccessExceptionWhenStateAccessedInReducer() {
        mockReducer.onReducerCall = { _, _ -> store.currentState + 3 }
        shouldThrow<UnsafeStateAccessException> {
            store.dispatch(KnownAction.A)
        }
    }

    @Test
    fun shouldThrowUnsafeDispatchExceptionWhenDispatchInReducer() {
        mockReducer.onReducerCall = { _, _ -> store.dispatch(KnownAction.B); 1 }
        shouldThrow<UnsafeDispatchException> {
            store.dispatch(KnownAction.A)
        }
    }

    @Test
    fun shouldCallReducerWithProperArguments() {
        store.dispatch(KnownAction.A)
        mockReducer.receivedAction shouldBe KnownAction.A
        mockReducer.receivedState shouldBe 1
    }

    @Test
    fun shouldCallReducerWhenNoMiddlewares() {
        store.dispatch(KnownAction.A)
        mockReducer.wasCalled shouldBe true
    }

    @Test
    fun shouldUpdateStateWithReducerReturnedValue() {
        store.dispatch(KnownAction.A)
        store.state.value shouldBe 2
    }

    @Test
    fun shouldReturnUpdatedStateAsCurrentState() {
        store.dispatch(KnownAction.A)
        store.currentState shouldBe 2
    }

    @Test
    fun shouldCallReducerAfterLastMiddlewareNext() {
        middlewares = listOf(middleware { next(it) })
        store.dispatch(KnownAction.A)
        mockReducer.wasCalled shouldBe true
    }

    @Test
    fun shouldNotCallReducerWhenLastMiddlewareNotCallingNext() {
        middlewares = listOf(middleware { })
        store.dispatch(KnownAction.A)
        mockReducer.wasCalled shouldBe false
    }

    @Test
    fun shouldCreateMiddlewareChainWithCorrectOrder() {
        val checkpoints = mutableListOf<Int>()
        middlewares = listOf(
            middleware { checkpoints.add(1); next(it) },
            middleware { checkpoints.add(2); next(it) },
            middleware { checkpoints.add(3); next(it) },
        )
        store.dispatch(KnownAction.A)
        checkpoints shouldBe listOf(1, 2, 3)
    }

    @Test
    fun shouldCreateMiddlewareChainThatCanBeStopped() {
        val checkpoints = mutableListOf<Int>()
        middlewares = listOf(
            middleware { checkpoints.add(1); next(it) },
            middleware { checkpoints.add(2); },
            middleware { checkpoints.add(3); next(it) },
        )
        store.dispatch(KnownAction.A)
        checkpoints shouldBe listOf(1, 2)
    }

    @Test
    fun shouldCreateMiddlewareChainThatCanChangePassedAction() {
        middlewares = listOf(
            middleware { next(KnownAction.B) },
            middleware { it shouldBe KnownAction.B },
        )
        store.dispatch(KnownAction.A)
    }

    @Test
    fun shouldDeliverUpdatedStateToMiddlewares() {
        middlewares = listOf(
            middleware { if (it == KnownAction.A) currentState shouldBe 3 else next(it) },
        )
        mockReducer.onReducerCall = { state, action -> if (action == KnownAction.B) 3 else state }
        store.dispatch(KnownAction.B)
        store.dispatch(KnownAction.A)
    }

    @Test
    fun shouldDeliverDispatchThatAllowsToCallDispatchProperly() {
        var dispatchFlag = false
        middlewares = listOf(
            middleware {
                when (it) {
                    KnownAction.A -> dispatch(KnownAction.B)
                    KnownAction.B -> dispatchFlag = true
                    else -> next(it)
                }
            },
        )
        store.dispatch(KnownAction.A)
        dispatchFlag shouldBe true
    }

    private fun storeShouldContainOriginalClosure() {
        store.closure.find(ForegroundJobRegistry).shouldBeInstanceOf<EmptyForegroundJobRegistry>()
        store.closure.find(LocalClosure).shouldNotBeNull()
    }
}

class MockReducer<State>(var onReducerCall: (State, Action) -> State) {

    var receivedState: State? = null
    var receivedAction: Action? = null
    var wasCalled = false

    fun call(state: State, action: Action): State {
        receivedState = state
        receivedAction = action
        wasCalled = true
        return onReducerCall(state, action)
    }
}