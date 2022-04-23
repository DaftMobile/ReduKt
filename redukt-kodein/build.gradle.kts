plugins {
    reduKtPlugins()
}

kotlin {
    reduKtSupportedTargets()
    sourceSets {
        reduKtOptIns()
        val commonMain by getting {
            dependencies {
                api(ReduKtProject.core)
                implementation(libs.kodein)
            }
        }
    }
}