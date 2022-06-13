package com.daftmobile.redukt.thunk.then

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.scope.DispatchScope
import com.daftmobile.redukt.thunk.ExecuteThunk

internal sealed class ThenStatement<Tail> : ExecuteThunk<Any?, Tail>() {
    override fun DispatchScope<Any?>.execute(): Tail = throw IllegalStateException("Statements should not be executed!")
}

@PublishedApi
internal class ThenProduceStatement<R>(val produce: (Any?) -> Action): ThenStatement<R>()

@PublishedApi
internal open class ThenCatchingStatement<R>(val catch: (Cause) -> Action?): ThenStatement<R>()

@PublishedApi
internal class ThenFinallyStatement<R>(
    val produce: (Result<Any?>) -> Action,
): ThenCatchingStatement<R>(catch = { (_, e) -> produce(Result.failure(e)) })