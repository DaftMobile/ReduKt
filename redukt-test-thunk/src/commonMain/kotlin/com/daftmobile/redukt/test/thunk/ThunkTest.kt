package com.daftmobile.redukt.test.thunk

import com.daftmobile.redukt.core.closure.DispatchClosure
import com.daftmobile.redukt.core.closure.EmptyDispatchClosure
import com.daftmobile.redukt.thunk.CoThunk
import com.daftmobile.redukt.thunk.Thunk

/**
 * Creates a [CoThunkTester] with given parameters and runs a single [CoThunkTester.test].
 */
public inline fun <T> CoThunk<T>.test(
    initialState: T,
    initialClosure: DispatchClosure = EmptyDispatchClosure,
    strict: Boolean = true,
    block: CoThunkTestScope<T>.() -> Unit,
): Unit = tester(initialState = initialState, initialClosure = initialClosure, strict = strict).test(block = block)

/**
 * Creates a [ThunkTester] with given parameters and runs a single [ThunkTester.test].
 */
public inline fun <T> Thunk<T>.test(
    initialState: T,
    initialClosure: DispatchClosure = EmptyDispatchClosure,
    strict: Boolean = true,
    block: ThunkTestScope<T>.() -> Unit,
): Unit = tester(initialState = initialState, initialClosure = initialClosure, strict = strict).test(block = block)
