plugins {
    id("redukt-lib")
}

dependencies {
    commonMainApi(project(":redukt-core"))
    commonTestImplementation(kotlin("test"))
}