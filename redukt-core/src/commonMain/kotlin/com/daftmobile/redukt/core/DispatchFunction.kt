package com.daftmobile.redukt.core

public interface Action

public typealias DispatchFunction = suspend (action: Action) -> Unit