plugins {
	id("java")
	id("idea")
	id("java-library")
}

version = "${loader}-${modVersion}+${minecraftVersion}"

base {
	archivesName = propOrNull("archives_base_name")
}

java {
	toolchain.languageVersion = JavaLanguageVersion.of(commonProject.prop("java_version"))
}

repositories {
    mavenCentral()
    exclusiveContent {
        forRepository {
            maven("https://repo.spongepowered.org/repository/maven-public") { name = "Sponge" }
        }
        filter { includeGroupAndSubgroups("org.spongepowered") }
    }
	maven("https://maven.terraformersmc.com/releases/") { name = "TerraformersMC" }
	maven("https://maven.shedaniel.me/")
	maven("https://maven.fabricmc.net/")
    maven {
        name = "Maven for PR #2815" // https://github.com/neoforged/NeoForge/pull/2815
        url = uri("https://prmaven.neoforged.net/NeoForge/pr2815")
        content {
            includeModule("net.neoforged", "neoforge")
            includeModule("net.neoforged", "testframework")
        }
    }
}

tasks {

	processResources {
		val expandProps = mapOf(
			"javaVersion" to versionedPropOrNull("java_version"),
			"modVersion" to modVersion,
			"minecraftVersion" to versionedPropOrNull("minecraft_version"),
		).filterValues { it?.isNotEmpty() == true }.mapValues { (_, v) -> v!! }

		val jsonExpandProps = expandProps.mapValues { (_, v) -> v.replace("\n", "\\\\n") }

			filesMatching(listOf("META-INF/mods.toml", "META-INF/neoforge.mods.toml")) {
				expand(expandProps)
			}

		filesMatching(listOf("pack.mcmeta", "fabric.mod.json", "*.mixins.json")) {
			expand(jsonExpandProps)
		}

		inputs.properties(expandProps)
	}
}

tasks.named("processResources") {
	dependsOn(common.project.tasks.named("stonecutterGenerate"))
}