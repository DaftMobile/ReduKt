package com.daftmobile.redukt.core

import com.daftmobile.redukt.core.closure.DispatchClosure
import com.daftmobile.redukt.core.closure.EmptyDispatchClosure

public interface ClosureScope {
    @DelicateReduKtApi
    public val closure: DispatchClosure
}

public interface LocalClosureScope : ClosureScope

public interface LocalDispatchScope<State> : DispatchScope<State>, LocalClosureScope

@InternalReduKtApi
public fun DispatchClosure.asLocalScope(): LocalClosureScope = LocalClosureScopeImpl(this)

@InternalReduKtApi
public inline fun <State> DispatchScope<State>.withLocalScope(
    closure: DispatchClosure = EmptyDispatchClosure,
    block: LocalDispatchScope<State>.() -> Unit
) {
    LocalDispatchScopeImpl(this.closure + closure, this).run(block)
}

@InternalReduKtApi
public fun <State> DispatchScope<State>.newLocalScope(closure: DispatchClosure = EmptyDispatchClosure): LocalDispatchScope<State> {
    return LocalDispatchScopeImpl(this.closure + closure, this)
}

internal class LocalClosureScopeImpl(@DelicateReduKtApi override val closure: DispatchClosure) : LocalClosureScope

@PublishedApi
internal class LocalDispatchScopeImpl<State>(
    localClosure: DispatchClosure,
    scope: DispatchScope<State>
) : LocalDispatchScope<State>, DispatchScope<State> by scope {
    override val closure: DispatchClosure = localClosure
}
