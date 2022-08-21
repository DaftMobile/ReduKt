plugins {
    id("redukt-lib")
}

dependencies {
    commonMainApi(ReduKt.test)
    commonMainApi(ReduKt.thunk)
    commonMainImplementation(kotlin("test"))
}
