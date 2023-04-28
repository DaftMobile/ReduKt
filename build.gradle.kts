import org.jetbrains.dokka.gradle.DokkaMultiModuleTask

plugins {
    alias(libs.plugins.kover)
    alias(libs.plugins.dokka)
    alias(libs.plugins.binarycheck)
}

buildscript {
    dependencies {
        classpath(":build-redukt")
        classpath(libs.gradle.plugin.kotlinx.atomicfu)
    }
}

val dokkaHtmlMultiModule by tasks.getting(DokkaMultiModuleTask::class)
val syncDocs by tasks.registering(Sync::class) {
    from(dokkaHtmlMultiModule.outputDirectory)
    into(rootProject.layout.projectDirectory.dir("docs"))
}

allprojects {
    group = "com.daftmobile.redukt"
    version = "1.0"
}
