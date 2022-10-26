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

        val allMainSets = listOf(jvmMain, jsMain, mingwX64Main, iosMain)

        val nonJsMain by creating {
            dependsOn(commonMain)
        }
        (allMainSets - jsMain).forEach {
            it.dependsOn(nonJsMain)
        }

    }
}

