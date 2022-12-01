package co.redukt.koin

import co.redukt.core.DispatchScope
import co.redukt.core.closure.DispatchClosure
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.component.KoinComponent


public interface KoinDI: KoinComponent, DispatchClosure.Element {

    override val key: Key get() = Key

    public companion object Key : DispatchClosure.Key<KoinDI>
}

public object GlobalKoinDI : KoinDI

public class KoinApplicationDI(
    public val app: KoinApplication
) : KoinDI {
    override fun getKoin(): Koin = app.koin
}

public inline val DispatchScope<*>.koin: Koin get() = closure[KoinDI].getKoin()
