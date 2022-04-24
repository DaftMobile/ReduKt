plugins {
    id("redukt-android-lib")
}

dependencies {
    commonMainApi(ReduKt.core)
    androidMainApi(libs.androix.viewmodel)
    commonMainImplementation(libs.kotlinx.coroutines.core)
}