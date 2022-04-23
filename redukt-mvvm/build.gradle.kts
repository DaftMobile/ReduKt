plugins {
    id("redukt-android-lib")
}

dependencies {
    commonMainApi(ReduKt.core)
    commonMainImplementation(libs.kotlinx.coroutines.core)
    androidMainImplementation(libs.androix.viewmodel)
}