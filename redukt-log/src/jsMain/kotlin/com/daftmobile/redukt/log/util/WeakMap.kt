package com.daftmobile.redukt.log.util

internal external class WeakMap<Key : Any, Value : Any> {

    fun has(key: Key): Boolean

    fun set(key: Key, obj: Value?)

    fun get(key: Key): Value?
}

internal inline fun <Key : Any, Value : Any> WeakMap<Key, Value>.getOrPut(
    key: Key,
    producer: () -> Value
) = when {
    !has(key) -> producer().also { set(key, it) }
    else -> get(key)!!
}