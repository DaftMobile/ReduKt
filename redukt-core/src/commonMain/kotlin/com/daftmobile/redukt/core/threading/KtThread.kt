package com.daftmobile.redukt.core.threading

/**
 * Represents a unified native thread.
 */
public sealed interface KtThread {

    /**
     * Name of the thread returned directly by the platform.
     * It might not be the best identification of a native thread (e.g. on JVM it might contain coroutine name).
     */
    public val rawName: String?

    /**
     * Name of the thread that is adjusted to identify native thread properly (e.g. removes coroutine name on JVM).
     * It returns [UNSPECIFIED] if [rawName] is null.
     */
    public val name: String
    public companion object {
        public const val UNSPECIFIED: String = "UNSPECIFIED"
    }
}

/**
 * Creates a [KtThread] with given [rawName]. It doesn't create a real native thread. It is just for thread identification.
 */
internal fun KtThread(rawName: String?): KtThread = KtThreadImpl(rawName)

private class KtThreadImpl(override val rawName: String?) : KtThread {

    override val name: String = rawName?.substringBeforeLast(" @") ?: KtThread.UNSPECIFIED

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as KtThreadImpl

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return rawName?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "Thread($name)"
    }
}