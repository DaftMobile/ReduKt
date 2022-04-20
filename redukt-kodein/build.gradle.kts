plugins {
    kotlin("multiplatform")
}

setupReduKtPackage()

kotlin {
    allSupportedTargets()
    sourceSets {
        commonOptIns()
        val commonMain by getting {
            dependencies {
                api(project(":redukt-core"))
                implementation(Libs.kodein.core)
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