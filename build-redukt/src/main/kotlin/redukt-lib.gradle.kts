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

    watchosArm32()
    watchosArm64()
    watchosX86()
    watchosSimulatorArm64()

    macosArm64()
    macosX64()

    // Other native
    mingwX64()

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

        val darwinMain by creating
        val darwinTest by creating { dependsOn(darwinMain) }

        val iosMain by getting { dependsOn(darwinMain) }
        val iosTest by getting
        val iosSimulatorArm64Main by getting {
            dependsOn(iosMain)
        }
        val iosSimulatorArm64Test by getting {
            dependsOn(iosTest)
        }

        val watchosX86Main by getting { dependsOn(darwinMain) }
        val watchosArm32Main by getting { dependsOn(darwinMain) }
        val watchosArm64Main by getting { dependsOn(darwinMain) }
        val watchosSimulatorArm64Main by getting { dependsOn(darwinMain) }

        val tvosMain by getting { dependsOn(darwinMain) }
        val tvosSimulatorArm64Main by getting { dependsOn(darwinMain) }

        val macosX64Main by getting { dependsOn(darwinMain) }
        val macosArm64Main by getting { dependsOn(darwinMain) }

        val allMainSets = listOf(jvmMain, jsMain, mingwX64Main, darwinMain)

        val nonJsMain by creating {
            dependsOn(commonMain)
        }
        (allMainSets - jsMain).forEach {
            it.dependsOn(nonJsMain)
        }

    }
}

