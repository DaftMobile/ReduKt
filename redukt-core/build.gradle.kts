plugins {
    id("redukt-lib")
}

dependencies {
    commonMainApi(libs.kotlinx.coroutines.core)

    commonTestImplementation(kotlin("test"))
    commonTestImplementation(libs.kotest.assertions.core)
    commonTestImplementation(libs.kotlinx.coroutines.test)
    commonTestImplementation(ReduKt.test)
}