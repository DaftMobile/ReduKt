package com.daftmobile.redukt.kodein

import com.daftmobile.redukt.core.ActionDispatcher
import com.daftmobile.redukt.core.context.DispatchContext
import org.kodein.di.DI

public class KodeinDI(di: DI): DI by di, DispatchContext.Element {

    public constructor(
        allowSilentOverride: Boolean = false,
        init: DI.MainBuilder.() -> Unit
    ) : this(DI(allowSilentOverride = allowSilentOverride, init = init))

    override val key: Key = Key

    public companion object Key : DispatchContext.Key<KodeinDI>
}

public inline val ActionDispatcher.di: DI get() = dispatchContext[KodeinDI]