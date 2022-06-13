package com.daftmobile.redukt.log.util

internal val genericToStringResult = object {}.toString()

internal val objectRefs = WeakMap<Any, String>()

internal var refCounter: UInt = 0u

internal fun nextRef() = refCounter++.toString(16)

internal actual fun Any.jsToString(identifyObjects: Boolean): String {
    val str = toString()
    val id: String = if (identifyObjects) objectRefs.getOrPut(this, ::nextRef) else "instance"
    return when (str) {
        genericToStringResult -> "${this::class.js.name}@$id"
        else -> str
    }
}