plugins {
    `maven-publish`
    signing
}

val dokkaJar by tasks.creating(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    archiveClassifier.set("javadoc")
    from(tasks.findByName("dokkaGfm"))
}
ext["signing.keyId"] = reduKtProperty("signingKeyId")
ext["signing.password"] = reduKtProperty("signingPassword")
ext["signing.secretKeyRingFile"] = reduKtProperty("signingSecretKeyRingFile")

signing {
    // disables signing for publishToMavenLocal
    setRequired { gradle.taskGraph.allTasks.any { it is PublishToMavenRepository } }
    sign(publishing.publications)
}

publishing {
    val ossrhUsername: String? by reduKtProperty
    val ossrhPassword: String? by reduKtProperty
    repositories {
        maven {
            name = "sonatype"
            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = ossrhUsername
                password = ossrhPassword
            }
        }
    }
    publications.withType<MavenPublication> {
        artifact(dokkaJar)
        pom {
            name.set(project.name)
            description.set(
                "ReduKt is a Redux pattern adaptation in Kotlin, integrated with coroutines, testable and multiplatform."
            )
            url.set(GitHttp)
            licenses {
                license {
                    name.set("MIT")
                    url.set("https://opensource.org/license/mit/")
                    distribution.set("repo")
                }
            }
            developers {
                developer {
                    id.set("DaftMobile")
                    name.set("DaftMobile")
                }
            }
            scm {
                url.set(GitHttp)
                connection.set(GitConnection)
                developerConnection.set(GitDevConnection)
            }
        }
    }
}

val GitBase = "github.com/DaftMobile/ReduKt"
val GitHttp = "https://$GitBase"
val GitConnection = "scm:git:git://$GitBase.git"
val GitDevConnection = "scm:git:ssh://git@$GitBase.git"
