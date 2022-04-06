setupReduKtPackage()

plugins {
    id("org.jetbrains.kotlinx.kover") version "0.5.0"
}

allprojects {
    repositories {
        mavenCentral()
    }
}