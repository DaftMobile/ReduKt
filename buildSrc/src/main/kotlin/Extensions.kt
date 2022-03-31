import org.gradle.api.DomainObjectCollection
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

fun KotlinMultiplatformExtension.allSupportedTargets() {
    jvm()
    js(BOTH) {
        browser()
        nodejs()
    }
    mingwX64()
}

fun Project.setupReduKtPackage() {
    group = ReduKt.group
    version = ReduKt.version
}

fun DomainObjectCollection<KotlinSourceSet>.commonOptIns() {
    all {
        languageSettings.optIn("kotlin.experimental.ExperimentalTypeInference")
    }
}