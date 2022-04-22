@file:Suppress("ClassName")

import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler
import kotlin.reflect.KProperty


val KotlinDependencyHandler.ReduKtProject get() = ReduKtProjectHelper(this)

class ReduKtProjectHelper(handler: KotlinDependencyHandler) : KotlinDependencyHandler by handler {

    val core by project()
    val thunk by project()
    val kodein by project()

}

private fun project() = ProjectDelegate()

private class ProjectDelegate {
    operator fun getValue(
        thisRef: ReduKtProjectHelper,
        property: KProperty<*>
    ) = thisRef.project(":redukt-${property.name}")
}