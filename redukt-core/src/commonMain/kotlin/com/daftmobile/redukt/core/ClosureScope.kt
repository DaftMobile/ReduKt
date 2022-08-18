package com.daftmobile.redukt.core

import com.daftmobile.redukt.core.closure.DispatchClosure

public interface ClosureScope {
    @DelicateReduKtApi
    public val closure: DispatchClosure
}

public interface LocalClosureScope : ClosureScope

public interface LocalDispatchScope<State> : DispatchScope<State>, LocalClosureScope

@PublishedApi
internal inline fun DispatchClosure.asLocalScope(): LocalClosureScope = LocalClosureScopeImpl(this)

@PublishedApi
internal class LocalClosureScopeImpl(@DelicateReduKtApi override val closure: DispatchClosure) : LocalClosureScope

@PublishedApi
internal inline fun <State> DispatchScope<State>.asLocalScope(
    closure: DispatchClosure
): LocalDispatchScope<State> = LocalDispatchScopeImpl(this.closure + closure, this)

@PublishedApi
internal inline fun DispatchFunction.invokeWithMergedClosure(
    originalClosure: DispatchClosure,
    localClosure: DispatchClosure,
    action: Action,
): Unit = invoke(LocalClosureScopeImpl(originalClosure + localClosure), action)

@PublishedApi
internal class LocalDispatchScopeImpl<State>(
    localClosure: DispatchClosure,
    scope: DispatchScope<State>
) : LocalDispatchScope<State>, DispatchScope<State> by scope {
    override val closure: DispatchClosure = localClosure
}