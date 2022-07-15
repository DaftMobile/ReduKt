plugins {
    id("redukt-lib")
}

dependencies {
    commonMainApi(ReduKt.test)
    commonMainApi(ReduKt.thunk)
    commonMainImplementation(libs.kotlinx.coroutines.core)
    commonMainImplementation(libs.kotlinx.coroutines.test)
    commonMainImplementation(kotlin("test"))
}