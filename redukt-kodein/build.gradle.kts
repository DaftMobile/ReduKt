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
                api(ReduKtProject.core)
                implementation(Libs.kodein.core)
            }
        }
    }
}