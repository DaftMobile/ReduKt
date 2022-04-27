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

inline infix fun <T, R, reified O : Thunk<R>> Thunk<T>.thenCatching(noinline catch: (Action, Throwable) -> O): Then<R> {
    val statement = ThenCatchingStatement<R>(catch) { "cause => ${O::class.simpleName}" }
    return Then(unwrapped() + statement)
}

@PublishedApi
internal fun Action.unwrapped() = if (this is Then<*>) actions else listOf(this)