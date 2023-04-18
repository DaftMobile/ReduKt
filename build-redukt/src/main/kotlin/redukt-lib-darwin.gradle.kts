@file:Suppress("UNUSED_VARIABLE")

plugins {
    id("redukt-lib-base")
}

kotlin {

    ios()
    iosSimulatorArm64()

    tvos()
    tvosSimulatorArm64()

    watchos()

    macosArm64()
    macosX64()

    sourceSets {
        val darwinMain by creating

        val iosMain by getting { dependsOn(darwinMain) }
        val iosTest by getting
        val iosSimulatorArm64Main by getting {
            dependsOn(iosMain)
        }
        val iosSimulatorArm64Test by getting {
            dependsOn(iosTest)
        }

        val watchosMain by getting { dependsOn(darwinMain) }

        val tvosMain by getting { dependsOn(darwinMain) }
        val tvosSimulatorArm64Main by getting { dependsOn(darwinMain) }

        val macosX64Main by getting { dependsOn(darwinMain) }
        val macosArm64Main by getting { dependsOn(darwinMain) }
    }
}

