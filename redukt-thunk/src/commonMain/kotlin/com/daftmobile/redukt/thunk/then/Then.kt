@file:Suppress("UNCHECKED_CAST")

package com.daftmobile.redukt.thunk.then

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.scope.DispatchScope
import com.daftmobile.redukt.thunk.SuspendThunk
import com.daftmobile.redukt.thunk.Thunk
import kotlinx.coroutines.suspendCancellableCoroutine

class Then<T> @PublishedApi internal constructor(val actions: List<Action>) : SuspendThunk<Nothing, T>() {

    override suspend fun DispatchScope<Nothing>.execute(): T = processActions(actions)

    private object SKIP

    private suspend fun DispatchScope<Nothing>.processActions(
        actions: List<Action>,
        lastValue: Any? = null
    ): T {
        var last: Any? = lastValue
        val iterator = actions.listIterator()
        while (iterator.hasNext()) {
            val action = iterator.next()
            val result = try {
                awaitDispatch(action, last)
            } catch (e: Throwable) {
                val catcher = iterator.skipUntilIs<ThenCatchingStatement<*>>() ?: throw e
                val catchAction = catcher.catch(Cause(action, e))
                if (catchAction != null) awaitDispatch(catchAction, last)
                else Unit
            }
            last = result.takeUnless { it === SKIP } ?: continue
        }
        return last as T
    }

    private tailrec suspend fun DispatchScope<Nothing>.awaitDispatch(action: Action, last: Any?): Any? {
        return when (action) {
            is ThenStatement<*> -> when (action) {
                is ThenProduceStatement<*> -> awaitDispatch(action.produce(last), last)
                is ThenFinallyStatement<*> -> awaitDispatch(action.produce(Result.success(last)), last)
                is ThenCatchingStatement<*> -> SKIP
            }
            is Thunk<*> -> suspendCancellableCoroutine {
                action.continuation = it
                dispatch(action)
            }
            else -> dispatch(action)
        }
    }

    private inline fun <reified T> Iterator<Action>.skipUntilIs(): T? {
        while (hasNext()) {
            val action = next()
            if (action is T) return action
        }
        return null
    }

    override fun toString(): String = actions.joinToString("then") { it.toStringPretty() }

    private fun Action.toStringPretty(): String = when (this) {
        is ThenProduceStatement<*> -> " { ${description()} }"
        is ThenCatchingStatement<*> -> "Catching { ${description()} }"
        is ThenFinallyStatement<*> -> "Finally { ${description()} }"
        else -> " $this"
    }
}