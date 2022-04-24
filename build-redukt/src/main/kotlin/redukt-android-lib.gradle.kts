plugins {
    kotlin("multiplatform")
    id("maven-publish")
    id("com.android.library")
}

kotlin {
    jvm()
    android {
        publishAllLibraryVariants()
        publishLibraryVariantsGroupedByFlavor = false
    }
    js(BOTH) {
        browser {
            testTask {
                useKarma {
                    useChromeHeadless()
                }
            }
        }
    }
    mingwX64()
    ios()
    sourceSets {
        all {
            languageSettings.optIn("kotlin.experimental.ExperimentalTypeInference")
        }

        val commonMain by getting

        val jvmMain by getting
        val jsMain by getting
        val mingwX64Main by getting
        val iosMain by getting

        val defaultMain by creating {
            dependsOn(commonMain)
        }

        listOf(jvmMain, jsMain, mingwX64Main, iosMain).forEach {
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