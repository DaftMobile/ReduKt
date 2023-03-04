import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.project
import java.util.Properties
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

val DependencyHandler.ReduKt get() = ReduKtSubprojects(this)

class ReduKtSubprojects(handler: DependencyHandler) : DependencyHandler by handler {
    val core by subproject()
    val test by subproject()
    val thunk by subproject()
    val data by subproject()
    val `test-thunk` by subproject()
}

fun subproject() = ReduKtSubprojectDelegate()

class ReduKtSubprojectDelegate {
    operator fun getValue(
        thisRef: ReduKtSubprojects,
        property: KProperty<*>
    ) = thisRef.project(":redukt-${property.name}")
}

/**
 * Property delegate for [reduKtProperty].
 */
val Project.reduKtProperty get() = ReadOnlyProperty<Any?, String?> { _, property -> reduKtProperty(property.name) }

/**
 * Provides a property from local.properties (by [name]) with fallback to environment variables (by [name] transformed
 * to snake case with all uppercase letters).
 * If [name] of property from local.properties is 'secretKey', then environment variable should be 'SECRET_KEY'.
 */
fun Project.reduKtProperty(name: String): String? = getLocalProperty(name)
    ?: System.getenv(name.camelToUpperSnakeCase())

private val camelRegex = "(?<=[a-zA-Z])[A-Z]".toRegex()

private fun String.camelToUpperSnakeCase(): String {
    return camelRegex.replace(this) {
        "_${it.value}"
    }.toUpperCase()
}

private fun Project.getLocalProperty(key: String): String? {
    val localPropertiesFile = project.rootProject.file("local.properties")
    if (!localPropertiesFile.exists()) {
        localPropertiesFile.createNewFile()
        return null
    }
    val properties = Properties()
    localPropertiesFile.inputStream().buffered().use(properties::load)
    return properties.getProperty(key)
}
