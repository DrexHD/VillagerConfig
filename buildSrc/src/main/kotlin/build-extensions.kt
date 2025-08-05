import dev.kikugie.stonecutter.build.StonecutterBuildExtension
import dev.kikugie.stonecutter.controller.StonecutterControllerExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.*

fun Project.prop(key: String) = requireNotNull(propOrNull(key)) { "Key $key does not exist in gradle.properties." }
fun Project.propOrNull(key: String) = findProperty(key)?.toString()
fun Project.versionedPropOrNull(key: String) = commonProject.propOrNull(key)
fun Project.versionedProp(key: String) = commonProject.prop(key)

val Project.stonecutterBuild get() = extensions.getByType<StonecutterBuildExtension>()
val Project.stonecutterController get() = extensions.getByType<StonecutterControllerExtension>()

val Project.common
    get() = requireNotNull(stonecutterBuild.node.sibling("common")) {
        "No common project for $project"
    }
val Project.commonProject get() = rootProject.project(stonecutterBuild.current.project)

val Project.modVersion get() = versionedProp("mod_version")
val Project.minecraftVersion
    get() = versionedPropOrNull("minecraft_version") ?: project.stonecutterBuild.current.version

val Project.loader get() = prop("loader")
val Project.loaderName get() = prop("loader_name")
val Project.modLoaders get() = prop("mod_loaders").split(", ")
val Project.modrinthMinecraftVersions
    get() = versionedProp("modrinth_minecraft_versions").split(", ")
val Project.curseforgeMinecraftVersions
    get() = versionedProp("curseforge_minecraft_versions").split(", ")

