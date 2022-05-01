@file:Suppress("UNCHECKED_CAST")

package com.daftmobile.redukt.thunk.then

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.thunk.Thunk

infix fun <T, R> Thunk<T>.then(other: Thunk<R>): Then<R> = Then(unwrapped() + other.unwrapped())

inline infix fun <reified T, R, reified O : Thunk<R>> Thunk<T>.then(noinline producer: (T) -> O): Then<R> {
    val statement = ThenProduceStatement<R>(producer as (Any?) -> Action) {
        "${T::class.simpleName} => ${O::class.simpleName}"
    }
    return Then(unwrapped() + statement)
}

data class Cause(val action: Action, val throwable: Throwable)

inline infix fun <T, R, reified O : Thunk<R>> Thunk<T>.thenCatching(noinline catch: (cause: Cause) -> O): Then<R> {
    val statement = ThenCatchingStatement<R>(catch) { "Cause => ${O::class.simpleName}" }
    return Then(unwrapped() + statement)
}

inline infix fun <reified T, R, reified O : Thunk<R>> Thunk<T>.thenFinally(
    noinline produce: (Result<T>) -> Thunk<R>
): Then<R> {
    val statement = ThenFinallyStatement<R>(produce = produce as ((Result<*>) -> Action)) {
        "Result<${T::class.simpleName}> => ${O::class.simpleName}"
    }
    return Then(unwrapped() + statement)
}

infix fun <T> Thunk<T>.thenOnlyCatch(catch: (cause: Cause) -> Unit): Then<Unit> {
    val statement = ThenCatchingStatement<Unit>(
        catch = { catch(it); null },
        description = { "Cause => Unit" }
    )
    return Then(unwrapped() + statement)
}

infix fun <T> Thunk<T>.thenDispatch(action: Action): Then<Unit> = Then(unwrapped() + action)

inline infix fun <reified T, reified O : Action> Thunk<T>.thenDispatch(noinline produce: (T) -> O): Then<Unit> {
    val statement = ThenProduceStatement<Unit>(produce as (Any?) -> Action) {
        "${T::class.simpleName} => ${O::class.simpleName}"
    }
    return Then(unwrapped() + statement)
}

inline infix fun <T, reified O : Action> Thunk<T>.thenDispatchCatching(noinline catch: (cause: Cause) -> O): Then<Unit> {
    val statement = ThenCatchingStatement<Unit>(catch) { "Cause => ${O::class.simpleName}" }
    return Then(unwrapped() + statement)
}

inline infix fun <reified T, reified O : Action> Thunk<T>.thenDispatchFinally(
    noinline produce: (Result<T>) -> O
): Then<Unit> {
    val statement = ThenFinallyStatement<Unit>(produce = produce as ((Result<*>) -> Action)) {
        "Result<${T::class.simpleName}> => ${O::class.simpleName}"
    }
    return Then(unwrapped() + statement)
}


@PublishedApi
internal fun Action.unwrapped() = if (this is Then<*>) actions else listOf(this)