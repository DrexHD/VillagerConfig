pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.neoforged.net/releases/")
        maven("https://maven.minecraftforge.net")
        maven("https://maven.kikugie.dev/snapshots")
        maven("https://maven.kikugie.dev/releases")
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.6+"
}

val fabricVersions = providers.gradleProperty("fabric_versions").orNull?.split(", ") ?: emptyList()
val neoforgeVersions = providers.gradleProperty("neoforge_versions").orNull?.split(", ") ?: emptyList()
val commonVersions = (fabricVersions union neoforgeVersions).toList()
val dists = mapOf(
    "common" to commonVersions,
    "fabric" to fabricVersions,
    "neoforge" to neoforgeVersions
)
val uniqueVersions = dists.values.flatten().distinct()

stonecutter {
    kotlinController = true
    centralScript = "build.gradle.kts"

    create(rootProject) {
        versions(*uniqueVersions.toTypedArray())

        dists.forEach { (branchName, branchVersions) ->
            branch(branchName) {
                versions(*branchVersions.toTypedArray())
            }
        }
    }
}

rootProject.name = "VillagerConfig"

