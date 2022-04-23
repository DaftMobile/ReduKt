plugins {
    id("org.kodein.mock.mockmp") version "1.4.0"
    id("redukt-lib")
}

mockmp {
    usesHelper = true
}

dependencies {
    commonMainImplementation(libs.kotlinx.coroutines.core)

    commonTestImplementation(kotlin("test"))
    commonTestImplementation(libs.kotest.assertions.core)
    commonTestImplementation(libs.kotlinx.coroutines.test)
}