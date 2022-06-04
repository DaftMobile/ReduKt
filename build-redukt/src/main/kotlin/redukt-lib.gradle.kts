plugins {
    kotlin("multiplatform")
    id("maven-publish")
}

kotlin {
    jvm()
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
    iosSimulatorArm64()
    sourceSets {
        all {
            languageSettings.optIn("kotlin.experimental.ExperimentalTypeInference")
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

