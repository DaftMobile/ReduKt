@file:Suppress("UNCHECKED_CAST")

package com.daftmobile.redukt.thunk.then

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.thunk.Thunk

fun <R> Action.then(other: Thunk<R>): Then<R> = ThenImpl(unwrapped() + other.unwrapped())

fun <T, R, O : Thunk<R>> Thunk<T>.then(producer: (T) -> O): Then<R> {
    val statement = ThenProduceStatement<R>(producer as (Any?) -> Action)
    return ThenImpl(unwrapped() + statement)
}

data class Cause(val action: Action, val throwable: Throwable)

fun <T, R, O : Thunk<R>> Thunk<T>.thenCatching(catch: (cause: Cause) -> O): Then<R> {
    val statement = ThenCatchingStatement<R>(catch)
    return ThenImpl(unwrapped() + statement)
}

fun <T, R,  O : Thunk<R>> Thunk<T>.thenFinally(produce: (Result<T>) -> O): Then<R> {
    val statement = ThenFinallyStatement<R>(produce = produce as ((Result<*>) -> Action))
    return ThenImpl(unwrapped() + statement)
}

fun <T> Thunk<T>.thenOnlyCatch(catch: (cause: Cause) -> Unit): Then<Unit> {
    val statement = ThenCatchingStatement<Unit>(
        catch = { catch(it); null },
    )
    return ThenImpl(unwrapped() + statement)
}

fun <T> Thunk<T>.thenAction(action: Action): Then<Unit> = ThenImpl(unwrapped() + action.unwrapped())

fun <T, O : Action> Thunk<T>.thenAction(produce: (T) -> O): Then<Unit> {
    val statement = ThenProduceStatement<Unit>(produce as (Any?) -> Action)
    return ThenImpl(unwrapped() + statement)
}

fun <T, O : Action> Thunk<T>.thenActionCatching(catch: (cause: Cause) -> O): Then<Unit> {
    val statement = ThenCatchingStatement<Unit>(catch)
    return ThenImpl(unwrapped() + statement)
}
fun <T, O : Action> Thunk<T>.thenActionFinally(produce: (Result<T>) -> O): Then<Unit> {
    val statement = ThenFinallyStatement<Unit>(produce = produce as ((Result<*>) -> Action))
    return ThenImpl(unwrapped() + statement)
}

internal fun Action.unwrapped() = when (this) {
    is NamedThen<*> -> listOf(this)
    is Then<*> -> actions
    else -> listOf(this)
}