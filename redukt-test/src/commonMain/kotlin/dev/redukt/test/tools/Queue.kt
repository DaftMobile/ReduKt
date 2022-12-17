package dev.redukt.test.tools

/**
 * Creates empty queue of type [T].
 */
public fun <T> emptyQueue(): Queue<T> = ListBasedQueue()

/**
 * Creates a queue of type [T] with initial [items] in a given order.
 */
public fun <T> queueOf(vararg items: T): Queue<T> = ListBasedQueue(items.toMutableList())

/**
 * A [Collection] that mutates in a FIFO manner.
 */
public interface Queue<T> : Collection<T> {

    /**
     * Puts an [item] at the end of the queue.
     */
    public fun push(item: T)

    /**
     * Removes first item from the queue and returns it. If there is no more elements in the queue, it throws an exception.
     */
    public fun pull(): T
}

/**
 * Removes first item from the queue and returns it. If there is no more elements in the queue, it returns null.
 */
public fun <T> Queue<T>.pullOrNull(): T? = firstOrNull()?.let { pull() }

public inline fun <T> Queue<T>.pullEach(block: (T) -> Unit) {
    while (true) {
        val action = pullOrNull() ?: return
        block(action)
    }
}

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
