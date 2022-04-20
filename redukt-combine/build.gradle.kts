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
                api(ReduKtProject.thunk)
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