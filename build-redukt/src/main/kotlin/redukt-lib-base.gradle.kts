plugins {
    kotlin("multiplatform")
    id("maven-publish")
    id("org.jetbrains.dokka")
    id("io.gitlab.arturbosch.detekt")
}

kotlin {
    explicitApi()
    sourceSets.all {
        languageSettings.apply {
            optIn("kotlin.experimental.ExperimentalTypeInference")
            optIn("com.daftmobile.redukt.core.DelicateReduKtApi")
        }
    }
}
