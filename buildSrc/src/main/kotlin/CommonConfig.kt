import org.gradle.api.DomainObjectCollection
import org.gradle.kotlin.dsl.kotlin
import org.gradle.plugin.use.PluginDependenciesSpec
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

fun PluginDependenciesSpec.reduKtPlugins() {
    kotlin("multiplatform")
}

fun KotlinMultiplatformExtension.reduKtSupportedTargets() {
    jvm()
    js(BOTH) {
        browser {
            testTask {
                useKarma {
                    useChromeHeadless()
                }
            }
        }
        nodejs()
    }
    mingwX64()
    ios()
}

fun DomainObjectCollection<KotlinSourceSet>.reduKtOptIns() {
    all {
        languageSettings.optIn("kotlin.experimental.ExperimentalTypeInference")
    }
}