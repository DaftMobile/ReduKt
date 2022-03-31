plugins {
    `kotlin-dsl`
}

val kotlinVersion: String by project

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(kotlin("gradle-plugin", "1.6.10"))
}