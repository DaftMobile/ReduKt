plugins {
    alias(libs.plugins.kover)
    alias(libs.plugins.dokka)
    alias(libs.plugins.binarycheck)
}

buildscript {
    dependencies {
        classpath(":build-redukt")
        classpath(libs.gradle.plugin.kotlinx.atomicfu)
    }
}

allprojects {
    group = "com.daftmobile.redukt"
    version = "1.0"
}
