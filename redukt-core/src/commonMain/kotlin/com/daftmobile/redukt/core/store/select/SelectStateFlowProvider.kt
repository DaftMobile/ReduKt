package com.daftmobile.redukt.core.store.select

import com.daftmobile.redukt.core.closure.DispatchClosure
import kotlinx.coroutines.flow.StateFlow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Provides a [StateFlow] for [Selector] to observe selected state.
 */
public interface SelectStateFlowProvider : DispatchClosure.Element {

    override val key: Key get() = Key

    /**
     * Provides a [StateFlow] that observes part of a [state] using [selector].
     */
    public fun <State, Selected> provide(
        state: StateFlow<State>,
        selector: Selector<State, Selected>
    ): StateFlow<Selected>

    public companion object Key : DispatchClosure.Key<SelectStateFlowProvider>
}

/**
 * Creates a [SelectStateFlowProvider] with given [cache] settings.
 */
public fun SelectStateFlowProvider(
    cache: SelectStateCache = SelectStateCache.Never
): SelectStateFlowProvider = when (cache) {
    SelectStateCache.Never -> NewSelectStateFlowProvider()
    is SelectStateCache.WhileSubscribed -> CachedSelectStateFlowProvider(
        newStateFlowProvider = NewSelectStateFlowProvider(),
        unusedFlowTimeout = cache.timeout,
    )
}

/**
 * Enumerates possible cache options for [SelectStateFlowProvider].
 */
public sealed interface SelectStateCache {

    /**
     * Never uses any cache. Always provides a new [StateFlow].
     */
    public object Never : SelectStateCache

    /**
     * Keeps [StateFlow] cached until it has subscribers. It's removed from cache after a [timeout] without subscribers.
     */
    public data class WhileSubscribed(val timeout: Duration = DEFAULT_TIMEOUT) : SelectStateCache {

        public companion object {

            public val DEFAULT_TIMEOUT: Duration = 10.seconds
        }
    }
}