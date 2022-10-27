package com.daftmobile.redukt.log

public fun interface LogOperator<in T> {

    public fun log(input: T)

}
