plugins {
    id("redukt-lib")
}

dependencies {
    commonMainApi(ReduKt.core)

    commonTestImplementation(kotlin("test"))
    commonTestImplementation(ReduKt.test)
    commonTestImplementation(libs.kotlinx.coroutines.test)
    commonTestImplementation(libs.kotest.assertions.core)
}