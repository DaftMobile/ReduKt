plugins {
    id("redukt-lib")
}

dependencies {
    commonMainApi(ReduKt.core)
    commonMainApi(libs.koin)
}