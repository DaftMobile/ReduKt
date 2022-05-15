plugins {
    id("redukt-lib")
}

dependencies {
    commonMainImplementation(libs.kotlinx.coroutines.core)

    commonTestImplementation(kotlin("test"))
    commonTestImplementation(libs.kotest.assertions.core)
    commonTestImplementation(libs.kotlinx.coroutines.test)
    commonTestImplementation(ReduKt.test)
}