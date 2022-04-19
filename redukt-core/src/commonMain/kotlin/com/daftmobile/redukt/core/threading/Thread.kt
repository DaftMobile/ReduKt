package com.daftmobile.redukt.core.threading

sealed interface Thread {
    val rawName: String?
    val name: String
    companion object {
        const val UNSPECIFIED = "UNSPECIFIED"
    }
}

fun Thread(rawName: String?): Thread = ThreadImpl(rawName)

private class ThreadImpl(override val rawName: String?) : Thread {

    override val name: String = rawName?.substringBeforeLast(" @") ?: Thread.UNSPECIFIED

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as ThreadImpl

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