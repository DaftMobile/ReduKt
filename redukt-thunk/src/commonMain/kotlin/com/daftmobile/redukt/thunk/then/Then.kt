@file:Suppress("UNCHECKED_CAST")

package com.daftmobile.redukt.thunk.then

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.scope.DispatchScope
import com.daftmobile.redukt.thunk.SuspendThunk
import com.daftmobile.redukt.thunk.Thunk
import kotlinx.coroutines.suspendCancellableCoroutine

abstract class Then<T> : SuspendThunk<Nothing, T>() {
    internal abstract val actions: List<Action>
}

@PublishedApi
internal class ThenImpl<T>(override val actions: List<Action>) : Then<T>() {

    override suspend fun DispatchScope<Nothing>.execute(): T = processActions(actions)

    private object SKIP

    private suspend fun DispatchScope<Nothing>.processActions(actions: List<Action>): T {
        var last: Any? = null
        val iterator = actions.listIterator()
        while (iterator.hasNext()) {
            val action = iterator.next()
            val result = try {
                awaitDispatch(action, last)
            } catch (e: Throwable) {
                processException(iterator, e, action, last)
            }
            last = result.takeUnless { it === SKIP } ?: continue
        }
        return last as T
    }

    private tailrec suspend fun DispatchScope<Nothing>.processException(
        iterator: Iterator<Action>,
        exception: Throwable,
        action: Action,
        last: Any?
    ): Any? {
        val catcher = iterator.skipUntilIs<ThenCatchingStatement<*>>() ?: throw exception
        val result = runCatching {
            val catchAction = catcher.catch(Cause(action, exception))
            if (catchAction != null) awaitDispatch(catchAction, last)
            else Unit
        }
        val nextException = result.exceptionOrNull()
        return if (nextException != null) {
            processException(iterator, nextException, action, last)
        } else {
            result.getOrNull()
        }
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

    override fun toString(): String = "#Then " + actions.joinToString(" then") { it.toStringPretty() }

    private fun Action.toStringPretty(): String = when (this) {
        is ThenProduceStatement<*> -> " { ${description()} }"
        is ThenCatchingStatement<*> -> "Catching { ${description()} }"
        is ThenFinallyStatement<*> -> "Finally { ${description()} }"
        else -> " $this"
    }
}