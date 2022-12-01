plugins {
    id("org.jetbrains.kotlinx.kover") version "0.5.0"
}

buildscript {
    dependencies {
        classpath(":build-redukt")
        classpath(libs.plugin.kotlinx.atomicfu)
    }
}

allprojects {
    group = "co.redukt"
    version = "1.0"
}