plugins {
    id("redukt-lib")
}

dependencies {
    commonMainApi(ReduKt.data)
    commonMainApi(libs.ktor.client.core)
}
