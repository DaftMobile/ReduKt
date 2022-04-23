plugins {
    id("redukt-lib")
}

dependencies {
    commonMainApi(project(":redukt-core"))
    commonMainImplementation(libs.kotlinx.coroutines.core)
}