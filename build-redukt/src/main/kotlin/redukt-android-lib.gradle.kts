
plugins {
    kotlin("multiplatform")
    id("maven-publish")
    id("com.android.library")
    id("org.jetbrains.dokka")
}

kotlin {
    explicitApi()

    jvm()
    android {
        publishAllLibraryVariants()
        publishLibraryVariantsGroupedByFlavor = true
    }
    js(IR) {
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
    iosSimulatorArm64()
    sourceSets {
        all {
            languageSettings.apply {
                optIn("kotlin.experimental.ExperimentalTypeInference")
                optIn("com.daftmobile.redukt.core.DelicateReduKtApi")
            }
        }

        val commonMain by getting

        val jvmMain by getting
        val jsMain by getting
        val mingwX64Main by getting
        val iosMain by getting
        val iosTest by getting
        val iosSimulatorArm64Main by getting {
            dependsOn(iosMain)
        }
        val iosSimulatorArm64Test by getting {
            dependsOn(iosTest)
        }

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