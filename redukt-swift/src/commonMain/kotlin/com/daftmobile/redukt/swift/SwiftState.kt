package com.daftmobile.redukt.swift

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

public abstract class SwiftState<out T> {

    public abstract val value: T

    public abstract fun subscribe(onChange: (T) -> Unit): Disposable
}

public abstract class RequiredSwiftState<out T : Any> : SwiftState<T>()

public fun <T> StateFlow<T>.asSwiftState(
    scope: CoroutineScope
): SwiftState<T> = SwiftStateFlow(scope, this)

public fun <T : Any> StateFlow<T>.asSwiftState(
    scope: CoroutineScope
): RequiredSwiftState<T> = RequiredSwiftStateFlow(scope, this)

private class SwiftStateFlow<T>(
    private val scope: CoroutineScope,
    private val state: StateFlow<T>
) : SwiftState<T>() {
    override val value: T = state.value

    override fun subscribe(onChange: (T) -> Unit): Disposable = Disposable(state.onEach(onChange).launchIn(scope)::cancel)

}

private class RequiredSwiftStateFlow<T : Any>(
    private val scope: CoroutineScope,
    private val state: StateFlow<T>
) : RequiredSwiftState<T>() {
    override val value: T = state.value

    override fun subscribe(onChange: (T) -> Unit): Disposable = Disposable(state.onEach(onChange).launchIn(scope)::cancel)

}