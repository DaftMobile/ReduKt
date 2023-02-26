plugins {
    alias(libs.plugins.kover)
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
