plugins {
    id("redukt-lib")
}

dependencies {
    commonMainApi(ReduKt.core)

    commonTestImplementation(kotlin("test"))
    commonTestImplementation(ReduKt.test)
}