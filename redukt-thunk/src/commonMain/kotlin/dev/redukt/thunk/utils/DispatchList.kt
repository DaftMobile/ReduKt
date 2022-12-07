package dev.redukt.thunk.utils

import dev.redukt.core.Action
import dev.redukt.core.DispatchScope
import dev.redukt.core.coroutines.ForegroundJobAction
import dev.redukt.core.coroutines.dispatchJobIn
import dev.redukt.core.coroutines.joinDispatchJob
import dev.redukt.thunk.CoThunkAction
import dev.redukt.thunk.ThunkAction
import kotlinx.coroutines.coroutineScope

/**
 * A [ThunkAction] that dispatches [actions] in given order. It should be created with [plus].
 */
public data class DispatchList(val actions: List<Action>) : ThunkAction<Unit> {
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
) : CoThunkAction<Any?> {
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
