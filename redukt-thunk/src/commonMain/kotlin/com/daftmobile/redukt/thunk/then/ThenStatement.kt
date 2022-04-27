package com.daftmobile.redukt.thunk.then

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.scope.DispatchScope
import com.daftmobile.redukt.thunk.ExecuteThunk

internal sealed class ThenStatement<Tail> : ExecuteThunk<Any?, Tail>() {
    override fun DispatchScope<Any?>.execute(): Tail = throw IllegalStateException("Statements should not be executed!")
}

@PublishedApi
internal class ThenProduceStatement<R>(
    val produce: (Any?) -> Action,
    val description: () -> String
): ThenStatement<R>() {
    override fun toString(): String = "ProduceStatement(${description()})"
}

@PublishedApi
internal class ThenCatchingStatement<R>(
    val catch: (action: Action, Throwable) -> Action,
    val description: () -> String
): ThenStatement<R>() {
    override fun toString(): String = "CatchingStatement(${description()})"
}