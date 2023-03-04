plugins {
    kotlin("multiplatform")
    id("org.jetbrains.dokka")
    id("io.gitlab.arturbosch.detekt")
    id("redukt-publish")
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
