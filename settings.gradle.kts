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
    }
}
includeBuild("build-redukt")
include(
    ":redukt-core",
    ":redukt-thunk",
    ":redukt-kodein",
    ":redukt-log",
)
