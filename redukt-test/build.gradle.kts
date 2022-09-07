plugins {
    id("redukt-lib")
}

dependencies {
    commonMainApi(ReduKt.core)
    commonMainApi(kotlin("test"))

    commonTestImplementation(kotlin("test"))
}
