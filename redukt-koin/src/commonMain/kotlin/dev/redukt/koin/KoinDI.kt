package dev.redukt.koin

import dev.redukt.core.DispatchScope
import dev.redukt.core.closure.DispatchClosure
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.component.KoinComponent


public interface KoinDI: KoinComponent, DispatchClosure.Element {

    override val key: Key get() = Key

    public companion object Key : DispatchClosure.Key<KoinDI>
}

public object GlobalKoinDI : KoinDI {
    override fun toString(): String = "GlobalKoinDI"
}

public class KoinApplicationDI(
    public val app: KoinApplication
) : KoinDI {
    override fun getKoin(): Koin = app.koin

    override fun toString(): String = "KoinApplicationDI($app)"
}

public inline val DispatchScope<*>.koin: Koin get() = closure[KoinDI].getKoin()
