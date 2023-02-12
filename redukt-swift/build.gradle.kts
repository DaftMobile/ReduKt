plugins {
    id("redukt-lib")
}

dependencies {
    commonMainApi(ReduKt.core)

    commonTestImplementation(libs.kotlinx.coroutines.test)
    commonTestImplementation(ReduKt.test)
    commonTestImplementation(kotlin("test"))
}