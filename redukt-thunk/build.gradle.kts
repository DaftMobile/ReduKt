plugins {
    id("redukt-lib")
}

dependencies {
    commonMainApi(ReduKt.core)

    commonTestImplementation(ReduKt.`test-thunk`)
    commonTestImplementation(kotlin("test"))
    commonTestImplementation(libs.kotlinx.coroutines.test)
    commonTestImplementation(libs.kotest.assertions.core)

}