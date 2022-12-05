plugins {
    id("redukt-lib")
    id("org.jetbrains.compose")
}

dependencies {
    commonMainApi(ReduKt.core)
    commonMainApi(libs.compose)
}