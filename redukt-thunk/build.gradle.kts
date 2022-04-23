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
                implementation(libs.kotlinx.coroutines.core)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.kotlinx.coroutines.test)
            }
        }
    }
}