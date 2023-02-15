plugins {
    id("redukt-lib-darwin")
}

dependencies {
    darwinMainApi(ReduKt.core)

    darwinTestImplementation(libs.kotlinx.coroutines.test)
    darwinTestImplementation(ReduKt.test)
    darwinTestImplementation(kotlin("test"))
}