plugins {
    id("redukt-lib")
    id("com.android.library")
}

kotlin {
    android {
        publishLibraryVariants("release")
    }
    sourceSets {

        val commonMain by getting

        val jvmMain by getting
        val jsMain by getting
        val mingwX64Main by getting
        val linuxX64Main by getting
        val darwinMain by getting

        val defaultMain by creating {
            dependsOn(commonMain)
        }

        listOf(jvmMain, jsMain, mingwX64Main, linuxX64Main, darwinMain).forEach {
            it.dependsOn(defaultMain)
        }
    }
}

android {
    compileSdk = 32
    defaultConfig {
        minSdk = 16
        targetSdk = 32
    }
}