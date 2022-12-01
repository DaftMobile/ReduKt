package dev.redukt.insight

public fun interface Inspection<out T> {

    public fun perform(inspector: Inspector<T>)
}

public inline fun <T> inspection(crossinline block: Inspector<T>.() -> Unit): Inspection<T> = Inspection { block(it) }

public inline fun Inspection<*>.inspect(): Unit = perform(NoOpInspector)

public inline fun <T, R> Inspection<T>.transform(
    crossinline block: Inspector<R>.(T) -> Unit
): Inspection<R> = inspection { perform { block(it) } }

public inline fun <T, R> Inspection<T>.map(crossinline mapper: (T) -> (R)): Inspection<R> = transform { inspect(mapper(it)) }

public inline fun <T> Inspection<T>.filter(crossinline predicate: (T) -> (Boolean)): Inspection<T> = transform {
    if (predicate(it)) inspect(it)
}

public inline fun <T> Inspection<T>.onEach(crossinline block: (T) -> Unit): Inspection<T> = transform {
    block(it)
    inspect(it)
}

public inline fun <T> Inspection<List<T>>.flatten(): Inspection<T> = transform { list ->
    list.forEach { inspect(it) }
}

@PublishedApi
internal val NoOpInspector: Inspector<Any?> = Inspector { }