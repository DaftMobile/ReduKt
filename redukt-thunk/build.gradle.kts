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
                api(project(":redukt-core"))
                implementation(Libs.kotlinx.coroutines.core)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(Libs.kotlinx.coroutines.test)
            }
        }
    }
}