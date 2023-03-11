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
    jvmTestImplementation("org.jetbrains.kotlinx:lincheck:2.16")
}
