plugins {
    reduKtPlugins()
    id("org.kodein.mock.mockmp") version "1.4.0"
}

mockmp {
    usesHelper = true
}

kotlin {
    reduKtSupportedTargets()
    sourceSets {
        reduKtOptIns()
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.kotest.assertions.core)
                implementation(libs.kotlinx.coroutines.test)
            }
        }
    }
}