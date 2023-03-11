plugins {
    id("redukt-publish")
    id("java-platform")
}

publishing.publications {
    create<MavenPublication>("maven") {
        from(components.findByName("javaPlatform"))
    }
}

dependencies {
    constraints {
        (rootProject.subprojects - project)
            .filter { it.plugins.hasPlugin("maven-publish") }
            .forEach {
                api("${rootProject.group}:${it.name}:${rootProject.version}").also(::println)
            }
    }
}
