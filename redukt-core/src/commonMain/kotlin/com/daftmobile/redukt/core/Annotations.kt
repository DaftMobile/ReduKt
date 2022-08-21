package com.daftmobile.redukt.core

@Retention(value = AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.TYPEALIAS, AnnotationTarget.PROPERTY)
@RequiresOptIn(
    level = RequiresOptIn.Level.ERROR, message = "This is an internal ReduKt API that " +
            "should not be used from outside of ReduKt library."
)
public annotation class InternalReduKtApi
