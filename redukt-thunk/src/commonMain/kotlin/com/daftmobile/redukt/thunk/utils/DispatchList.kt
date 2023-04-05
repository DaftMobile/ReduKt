package com.daftmobile.redukt.thunk.utils

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.DispatchScope
import com.daftmobile.redukt.core.coroutines.ForegroundJobAction
import com.daftmobile.redukt.core.coroutines.dispatchJobIn
import com.daftmobile.redukt.core.coroutines.joinDispatchJob
import com.daftmobile.redukt.thunk.CoThunk
import com.daftmobile.redukt.thunk.Thunk
import com.daftmobile.redukt.thunk.ThunkMarker
import kotlinx.coroutines.coroutineScope

/**
 * A [ThunkMarker] that dispatches [actions] in given order. It should be created with [plus].
 */
public data class DispatchList(val actions: List<Action>) : Thunk<Unit> {
    override fun DispatchScope<Unit>.execute() {
        actions.forEach(::dispatch)
    }
}

/**
 * A [CoThunkAction] that dispatches [actions] in given order.
 * If [concurrent] is false, every [ForegroundJobAction] is joined sequentially.
 * If [concurrent] is true, [actions] are dispatched concurrently and [execute] suspends until every [ForegroundJobAction] is completed.
 */
public data class JoiningCoroutinesDispatchList(
    val actions: List<Action>,
    val concurrent: Boolean
) : CoThunk<Any?> {
    override suspend fun DispatchScope<Any?>.execute() {
        if (concurrent) coroutineScope {
            actions.forEach {
                if (it is ForegroundJobAction) dispatchJobIn(it, this)
                else dispatch(it)
            }
        } else {
            actions.forEach {
                if (it is ForegroundJobAction) joinDispatchJob(it)
                else dispatch(it)
            }
        }
    }
}

/**
 * Creates a [DispatchList] with [this] action and [other]. This operator automatically flattens [DispatchList],
 * so a chain of calls results in a single and flat [DispatchList].
 */
public operator fun Action.plus(other: Action): DispatchList = DispatchList(unwrapped() + other.unwrapped())

/**
 * Transforms [DispatchList] to [JoiningCoroutinesDispatchList] according to [support] object.
 */
public infix fun DispatchList.support(support: DispatchListSupport.JoiningCoroutines): JoiningCoroutinesDispatchList {
    return JoiningCoroutinesDispatchList(actions, support.concurrent)
}

/**
 * Namespace for [DispatchList] config classes.
 */
public interface DispatchListSupport {

    /**
     * Configures [JoiningCoroutinesDispatchList].
     */
    public data class JoiningCoroutines(val concurrent: Boolean = false)
}

private fun Action.unwrapped(): List<Action> = if (this is DispatchList) actions else listOf(this)
