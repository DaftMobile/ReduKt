plugins {
    kotlin("multiplatform")
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
    sourceSets {
        all {
            languageSettings.optIn("kotlin.experimental.ExperimentalTypeInference")
        }
    }
}

