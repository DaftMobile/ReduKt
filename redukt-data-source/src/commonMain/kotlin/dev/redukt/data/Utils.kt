package dev.redukt.data

@PublishedApi
internal inline fun <reified T> Any?.cast(): T = this as T