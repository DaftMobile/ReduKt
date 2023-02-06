package co.redukt.core

@Retention(value = AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.TYPEALIAS, AnnotationTarget.PROPERTY)
@RequiresOptIn(
    level = RequiresOptIn.Level.ERROR, message = "This is a delicate ReduKt API and its use requires care." +
            " Make sure you fully read and understand documentation of the declaration that is marked as a delicate API."
)
public annotation class DelicateReduKtApi
