plugins {
    id("redukt-lib")
}

dependencies {
    commonMainApi(ReduKt.core)
    commonMainImplementation(libs.kodein)
}