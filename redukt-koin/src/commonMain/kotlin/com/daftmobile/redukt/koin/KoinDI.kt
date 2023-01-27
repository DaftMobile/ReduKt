package com.daftmobile.redukt.koin

import com.daftmobile.redukt.core.DispatchScope
import com.daftmobile.redukt.core.closure.DispatchClosure
import com.daftmobile.redukt.koin.KoinDI.Key
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.component.KoinComponent

/**
 * Injects Koin into dispatch closure. It can be accessed by the [Key] from [DispatchClosure] or by [koin] property from [DispatchScope].
 */
public interface KoinDI : KoinComponent, DispatchClosure.Element {

    override val key: Key get() = Key

    public companion object Key : DispatchClosure.Key<KoinDI>
}

/**
 * Delegates to global Koin instance (initialized with `startKoin { ... }` function).
 */
public object GlobalKoinDI : KoinDI {
    override fun toString(): String = "GlobalKoinDI"
}

/**
 * Delegates to provided koin [app].
 */
public class KoinApplicationDI(
    public val app: KoinApplication
) : KoinDI {
    override fun getKoin(): Koin = app.koin

    override fun toString(): String = "KoinApplicationDI($app)"
}

/**
 * Returns Koin instance associated with a store. It depends on [KoinDI] element injected into the closure.
 */
public inline val DispatchScope<*>.koin: Koin get() = closure[KoinDI].getKoin()
