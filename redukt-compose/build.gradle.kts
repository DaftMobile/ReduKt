plugins {
    id("redukt-lib")
    id("org.jetbrains.compose")
}

compose {
    kotlinCompilerPlugin.set("androidx.compose.compiler:compiler:1.4.3")
}

dependencies {
    commonMainApi(ReduKt.core)
    commonMainApi(libs.compose)
}
