package com.daftmobile.redukt.test.tools

fun <T> emptyQueue(): Queue<T> = ListBasedQueue()

fun <T> queueOf(vararg items: T): Queue<T> = ListBasedQueue(items.toMutableList())

interface Queue<T> : Collection<T> {

    fun push(item: T)

    fun pull(): T
}

fun <T> Queue<T>.pullOrNull() = firstOrNull()?.let { pull() }

private class ListBasedQueue<T>(private val list: MutableList<T> = mutableListOf()) : Queue<T> {

    override fun push(item: T) {
        list.add(item)
    }

    override fun pull(): T = list.removeFirst()

    override val size: Int get() = list.size

    override fun isEmpty(): Boolean = list.isEmpty()

    override fun iterator(): Iterator<T> = list.iterator()

    override fun containsAll(elements: Collection<T>): Boolean = list.containsAll(elements)

    override fun contains(element: T): Boolean = list.contains(element)
}
