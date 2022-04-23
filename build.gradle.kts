plugins {
    id("org.jetbrains.kotlinx.kover") version "0.5.0"
}

setupReduKtPackage()

allprojects {
    repositories {
        mavenCentral()
    }
}