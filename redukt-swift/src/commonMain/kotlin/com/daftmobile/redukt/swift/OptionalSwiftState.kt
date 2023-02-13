@file:OptIn(ExperimentalObjCName::class)

package com.daftmobile.redukt.swift

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.experimental.ExperimentalObjCName
import kotlin.native.ObjCName

@ObjCName("ReduKtOptionalState", exact = true)
public abstract class OptionalSwiftState<out T> {

    public abstract val value: T

    public abstract fun subscribe(onChange: (T) -> Unit): Disposable
}

@ObjCName("ReduKtState", exact = true)
public abstract class SwiftState<out T : Any> {

    public abstract val value: T

    public abstract fun subscribe(onChange: (T) -> Unit): Disposable
}

public fun <T> StateFlow<T>.asSwiftState(
    scope: CoroutineScope
): OptionalSwiftState<T> = OptionalSwiftStateFlow(scope, this)

public fun <T : Any> StateFlow<T>.asSwiftState(
    scope: CoroutineScope
): SwiftState<T> = SwiftStateFlow(scope, this)

private class OptionalSwiftStateFlow<T>(
    private val scope: CoroutineScope,
    private val state: StateFlow<T>
) : OptionalSwiftState<T>() {
    override val value: T = state.value

    override fun subscribe(onChange: (T) -> Unit): Disposable = Disposable(state.onEach(onChange).launchIn(scope)::cancel)

}

private class SwiftStateFlow<T : Any>(
    private val scope: CoroutineScope,
    private val state: StateFlow<T>
) : SwiftState<T>() {

    override val value: T = state.value

    override fun subscribe(onChange: (T) -> Unit): Disposable = Disposable(state.onEach(onChange).launchIn(scope)::cancel)

}