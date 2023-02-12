plugins {
    `kotlin-dsl`
}

java.apply {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    api(libs.gradle.plugin.compose)
    api(libs.gradle.plugin.kotlin)
    api(libs.gradle.plugin.dokka)
}