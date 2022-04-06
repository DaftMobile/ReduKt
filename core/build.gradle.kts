import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile

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
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${ReduKt.vCoroutines}")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:${ReduKt.vCoroutines}")
            }
        }
    }
}