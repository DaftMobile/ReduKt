plugins {
    id("redukt-lib")
    id("kotlinx-atomicfu")
}

dependencies {
    commonMainApi(libs.kotlinx.coroutines.core)

    commonTestImplementation(kotlin("test"))
    commonTestImplementation(libs.kotest.assertions.core)
    commonTestImplementation(libs.kotlinx.coroutines.test)
    commonTestImplementation(libs.turbine)
    commonTestImplementation(ReduKt.test)
}