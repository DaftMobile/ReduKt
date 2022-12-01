@file:Suppress("UNUSED_VARIABLE")

plugins {
    kotlin("multiplatform")
    id("maven-publish")
    id("org.jetbrains.dokka")
}

kotlin {
    explicitApi()

    jvm()
    js(IR) {
        browser {
            testTask {
                useKarma {
                    useChromeHeadless()
                }
            }
        }
    }

    // Darwin
    ios()
    iosSimulatorArm64()

    tvos()
    tvosSimulatorArm64()

    watchos()
    watchosX86()

    macosArm64()
    macosX64()

    // Windows
    mingwX64()

    // Linux
    linuxX64()

    sourceSets {
        all {
            languageSettings.apply {
                optIn("kotlin.experimental.ExperimentalTypeInference")
                optIn("dev.redukt.core.DelicateReduKtApi")
            }
        }
        val commonMain by getting
        val commonTest by getting
        val jvmMain by getting
        val jsMain by getting
        val mingwX64Main by getting
        val linuxX64Main by getting

        val darwinMain by creating {
            dependsOn(commonMain)
        }

        val iosMain by getting { dependsOn(darwinMain) }
        val iosTest by getting
        val iosSimulatorArm64Main by getting {
            dependsOn(iosMain)
        }
        val iosSimulatorArm64Test by getting {
            dependsOn(iosTest)
        }

        val watchosMain by getting { dependsOn(darwinMain) }
        val watchosX86Main by getting { dependsOn(watchosMain) }

        val tvosMain by getting { dependsOn(darwinMain) }
        val tvosSimulatorArm64Main by getting { dependsOn(darwinMain) }

        val macosX64Main by getting { dependsOn(darwinMain) }
        val macosArm64Main by getting { dependsOn(darwinMain) }
    }
}

