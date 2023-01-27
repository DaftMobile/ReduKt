package com.daftmobile.redukt.test.tools

/**
 * Creates empty queue of type [T].
 */
public fun <T> emptyQueue(): Queue<T> = DequeBasedQueue()

/**
 * Creates a queue of type [T] with initial [items] in a given order.
 */
public fun <T> queueOf(vararg items: T): Queue<T> = DequeBasedQueue(ArrayDeque(items.toList()))

/**
 * Transforms collection into [Queue].
 */
public fun <T> Collection<T>.toQueue(): Queue<T> = DequeBasedQueue(ArrayDeque(this))

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

    /**
     * Removes first item from the queue and returns it. If there is no more elements in the queue, it returns null.
     */
    public fun pullOrNull(): T?
}


public inline fun <T> Queue<T>.pullEach(block: (T) -> Unit) {
    while (true) {
        val action = pullOrNull() ?: return
        block(action)
    }
}

private class DequeBasedQueue<T>(private val deque: ArrayDeque<T> = ArrayDeque()) : Queue<T> {

    override fun push(item: T) {
        deque.add(item)
    }

    override fun pull(): T = deque.removeFirst()
    override fun pullOrNull(): T? = deque.removeFirstOrNull()

    override val size: Int get() = deque.size

    override fun isEmpty(): Boolean = deque.isEmpty()

    override fun iterator(): Iterator<T> = deque.iterator()

    override fun containsAll(elements: Collection<T>): Boolean = deque.containsAll(elements)

    override fun contains(element: T): Boolean = deque.contains(element)
}
