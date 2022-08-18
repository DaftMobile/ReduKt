package com.daftmobile.redukt.core

public interface Action

public interface SuspendAction : Action

public typealias DispatchFunction = LocalClosureScope.(action: Action) -> Unit