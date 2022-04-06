package com.github.lupuuss.redukt.core

internal sealed class KnownAction : Action {
    object A : KnownAction()
    object B : KnownAction()
}

internal object UnknownAction : Action