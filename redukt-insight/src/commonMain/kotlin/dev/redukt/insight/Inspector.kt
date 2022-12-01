package dev.redukt.insight

public fun interface Inspector<in T> {

    public fun inspect(input: T)
}
