plugins {
    id("redukt-lib-base")
}

kotlin {

    ios()
    iosSimulatorArm64()

    tvos()
    tvosSimulatorArm64()

    watchos()
    watchosX86()

    macosArm64()
    macosX64()

    sourceSets {
        val darwinMain by creating
        val darwinTest by creating

        val iosMain by getting { dependsOn(darwinMain) }
        val iosTest by getting
        val iosSimulatorArm64Main by getting {
            dependsOn(iosMain)
        }
        val iosSimulatorArm64Test by getting {
            dependsOn(iosTest)
        }

        val watchosMain by getting { dependsOn(darwinMain) }
        val watchosX86Main by getting { dependsOn(watchosMain) }

        val tvosMain by getting { dependsOn(darwinMain) }
        val tvosSimulatorArm64Main by getting { dependsOn(darwinMain) }

        val macosX64Main by getting { dependsOn(darwinMain) }
        val macosArm64Main by getting { dependsOn(darwinMain) }
    }
}

