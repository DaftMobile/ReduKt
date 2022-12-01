package dev.redukt.core.store

public fun interface SelectorEquality<in T> {
    public fun isEqual(old: T, new: T): Boolean

    public companion object {
        public val Default: SelectorEquality<Any?> = SelectorEquality { o, n -> o == n }

        public inline fun <T, R> by(crossinline property: (T) -> R): SelectorEquality<T> = SelectorEquality { old, new ->
            old.let(property) == new.let(property)
        }
    }
}