plugins {
    `kotlin-dsl`
}

java.apply {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    api(libs.plugin.compose)
    api(libs.plugin.kotlin)
    api(libs.plugin.dokka)
}