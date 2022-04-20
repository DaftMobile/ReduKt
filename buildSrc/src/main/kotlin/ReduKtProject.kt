@file:Suppress("ClassName")

import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler
import kotlin.reflect.KProperty


val KotlinDependencyHandler.ReduKtProject get() = ReduKtProjectHelper(this)

class ReduKtProjectHelper(handler: KotlinDependencyHandler) : KotlinDependencyHandler by handler {

    val core by subproject()
    val thunk by subproject()
    val combine by subproject()
    val kodein by subproject()

}

private fun subproject() = SubprojectDelegate()

private class SubprojectDelegate {
    operator fun getValue(
        thisRef: ReduKtProjectHelper,
        property: KProperty<*>
    ) = thisRef.project(":redukt-${property.name}")
}