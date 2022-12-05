package dev.redukt.compose


import androidx.compose.runtime.*
import dev.redukt.core.DispatchFunction
import dev.redukt.core.store.Selector
import dev.redukt.core.store.SelectorFunction
import dev.redukt.core.store.Store
import dev.redukt.core.store.select
import kotlin.properties.ReadOnlyProperty

/**
 * Creates static [CompositionLocal] for a [Store] with a given state [T].
 */
public fun <T> localStoreOf(
    defaultFactory: () -> Store<T> = { null!! }
): ProvidableCompositionLocal<Store<T>> = staticCompositionLocalOf(defaultFactory = defaultFactory)

public val <T> CompositionLocal<Store<T>>.dispatch: ReadOnlyProperty<Any?, DispatchFunction>
    @Composable get() = current.let { remember(it) { ReadOnlyProperty { _, _ -> it::dispatch } } }

/**
 * Selects part of a state using given [selector] as a Compose [State].
 */
@Composable
public fun <AppState, Selected> CompositionLocal<Store<AppState>>.select(
    selector: Selector<AppState, Selected>
): State<Selected> = current.let { store ->
    remember(store) { store.select(selector) }.collectAsState()
}

/**
 * Selects part of a state using given [selectorFunction] as a Compose [State].
 */
@Composable
public fun <AppState, Selected> CompositionLocal<Store<AppState>>.select(
    selectorFunction: SelectorFunction<AppState, Selected>,
): State<Selected> = current.let { store ->
    remember(store) { store.select(selectorFunction) }.collectAsState()
}


/**
 * Returns state as a Compose [State]
 */
public val <AppState> CompositionLocal<Store<AppState>>.state: State<AppState>
    @Composable get() = current.state.collectAsState()
