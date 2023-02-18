@file:Suppress("UNUSED_VARIABLE")

plugins {
    id("redukt-lib-base")
    id("redukt-lib-darwin")
}

kotlin {
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

    // Windows
    mingwX64()

    // Linux
    linuxX64()

    sourceSets {
        all {
            languageSettings.apply {
                optIn("kotlin.experimental.ExperimentalTypeInference")
                optIn("com.daftmobile.redukt.core.DelicateReduKtApi")
            }
        }
        val commonMain by getting

        val darwinMain by getting {
            dependsOn(commonMain)
        }
    }
}

