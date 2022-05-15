import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.project
import kotlin.reflect.KProperty

val DependencyHandler.ReduKt get() = ReduKtSubprojects(this)

class ReduKtSubprojects(handler: DependencyHandler): DependencyHandler by handler {
    val core by subproject()
    val test by subproject()
    val thunk by subproject()
}

fun subproject() = ReduKtSubprojectDelegate()

class ReduKtSubprojectDelegate {
    operator fun getValue(thisRef: ReduKtSubprojects, property: KProperty<*>) = thisRef.project(":redukt-${property.name}")
}
