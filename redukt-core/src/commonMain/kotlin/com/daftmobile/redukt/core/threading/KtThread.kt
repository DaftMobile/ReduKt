package com.daftmobile.redukt.core.threading

sealed interface KtThread {
    val rawName: String?
    val name: String
    companion object {
        const val UNSPECIFIED = "UNSPECIFIED"
    }
}

fun KtThread(rawName: String?): KtThread = KtThreadImpl(rawName)

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