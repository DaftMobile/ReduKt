package com.daftmobile.redukt.test.tools

public fun <T> emptyQueue(): Queue<T> = ListBasedQueue()

public fun <T> queueOf(vararg items: T): Queue<T> = ListBasedQueue(items.toMutableList())

public interface Queue<T> : Collection<T> {

    public fun push(item: T)

    public fun pull(): T
}

public fun <T> Queue<T>.pullOrNull(): T? = firstOrNull()?.let { pull() }

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
