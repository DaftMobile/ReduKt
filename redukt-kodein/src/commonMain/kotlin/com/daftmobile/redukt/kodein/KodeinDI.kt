package com.daftmobile.redukt.kodein

import com.daftmobile.redukt.core.ClosureScope
import com.daftmobile.redukt.core.closure.DispatchClosure
import org.kodein.di.DI

public class KodeinDI(di: DI): DI by di, DispatchClosure.Element {

    public constructor(
        allowSilentOverride: Boolean = false,
        init: DI.MainBuilder.() -> Unit
    ) : this(DI(allowSilentOverride = allowSilentOverride, init = init))

    override val key: Key = Key

    public companion object Key : DispatchClosure.Key<KodeinDI>
}

public inline val ClosureScope.di: DI get() = closure[KodeinDI]