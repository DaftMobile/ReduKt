plugins {
    id("org.jetbrains.kotlinx.kover") version "0.5.0"
}

buildscript {
    dependencies {
        classpath(":build-redukt")
    }
}

allprojects {
    group = "com.daftmobile.redukt"
    version = "1.0"
}