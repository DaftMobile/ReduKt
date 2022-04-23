plugins {
    kotlin("multiplatform")
    id("org.kodein.mock.mockmp") version "1.4.0"
}

setupReduKtPackage()

mockmp {
    usesHelper = true
}

kotlin {
    allSupportedTargets()
    sourceSets {
        commonOptIns()
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