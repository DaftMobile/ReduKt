package com.daftmobile.redukt.kodein

import com.daftmobile.redukt.core.ActionDispatcher
import com.daftmobile.redukt.core.context.DispatchContext
import org.kodein.di.DI

class KodeinDI(di: DI): DI by di, DispatchContext.Element {

    constructor(
        allowSilentOverride: Boolean = false,
        init: DI.MainBuilder.() -> Unit
    ) : this(DI(allowSilentOverride = allowSilentOverride, init = init))

    override val key = Key

    companion object Key : DispatchContext.Key<KodeinDI>
}

inline val ActionDispatcher.di get(): DI = dispatchContext[KodeinDI]