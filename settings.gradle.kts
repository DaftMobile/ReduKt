enableFeaturePreview("VERSION_CATALOGS")

rootProject.name = "ReduKt"

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }
}
includeBuild("build-redukt")
include(
    ":redukt-core",
    ":redukt-thunk",
    ":redukt-koin",
    ":redukt-test",
    ":redukt-test-thunk",
    ":redukt-data-source"
)
