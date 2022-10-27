package com.daftmobile.redukt.log

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.threading.KtThread
import com.daftmobile.redukt.core.threading.current

public fun interface Logger<out T> {

    public fun assemble(operator: LogOperator<T>)
}

public inline fun Logger<*>.assemble(): Unit = assemble { }

public inline fun <T> logger(crossinline block: LogOperator<T>.() -> Unit): Logger<T> = Logger { block(it) }

public inline fun <T, R> Logger<T>.map(crossinline mapper: (T) -> (R)): Logger<R> = logger {
    assemble {
        log(mapper(it))
    }
}

public inline fun <T> Logger<T>.filter(crossinline predicate: (T) -> (Boolean)): Logger<T> = logger {
    assemble {
        if (predicate(it)) log(it)
    }
}

public inline fun <T> Logger<T>.onEach(crossinline block: (T) -> Unit): Logger<T> = logger { assemble { block(it) } }

public inline fun <T> Logger<T>.printToSystemOut(): Logger<T> = onEach { println(it) }

public inline fun <T> Logger<T>.mapToString(): Logger<String> = map { it.toString() }

public inline fun Logger<String>.splitting(delimiter: String): Logger<String> = logger {
    assemble {
        it.split(delimiter)
    }
}

public inline fun Logger<String>.prependWithThreadName(): Logger<String> = prependWith("[${KtThread.current().name}]")

public inline fun Logger<String>.prependWith(value: String): Logger<String> = map { "$value$it" }

public inline fun Logger<String>.appendWith(value: String): Logger<String> = map { "$it$value" }

public inline fun Logger<LogContext<*>>.selectAction(): Logger<Action> = map { it.action }

public inline fun <State> Logger<LogContext<State>>.selectState(): Logger<State> = map { it.scope.currentState }