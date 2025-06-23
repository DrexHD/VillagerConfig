import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.ChangelogPluginExtension

plugins {
	id("java")
	id("idea")
    id("multiloader-common")
    id("org.jetbrains.changelog")
    id("me.modmuss50.mod-publish-plugin")
}

val commonJava: Configuration by configurations.creating {
    isCanBeResolved = true
}
val commonResources: Configuration by configurations.creating {
    isCanBeResolved = true
}

dependencies {
    val commonPath = common.hierarchy.toString()
	compileOnly(project(path = commonPath))
    commonJava(project(path = commonPath, configuration = "commonJava"))
    commonResources(project(path = commonPath, configuration = "commonResources"))
}

tasks {
    compileJava {
        dependsOn(commonJava)
        source(commonJava)
    }

    processResources {
        dependsOn(commonResources)
        from(commonResources)
    }

    publishMods {
        type.set(STABLE)
        changelog.set(fetchChangelog())

        displayName.set("VillagerConfig $loaderName $modVersion+$minecraftVersion")
        modLoaders.addAll(project.modLoaders)

        curseforge {
            accessToken.set(providers.environmentVariable("CURSEFORGE_TOKEN"))
            projectId.set("400741")
            minecraftVersions.addAll(curseforgeMinecraftVersions)
        }

        modrinth {
            accessToken.set(providers.environmentVariable("MODRINTH_TOKEN"))
            projectId.set("OClpEDe3")
            minecraftVersions.addAll(modrinthMinecraftVersions)
        }

        github {
            accessToken.set(providers.environmentVariable("GITHUB_TOKEN"))
            repository.set(providers.environmentVariable("GITHUB_REPOSITORY").orElse("DrexHD/VillagerConfig"))
            commitish.set(providers.environmentVariable("GITHUB_REF_NAME").orElse("main"))
        }
    }
}

fun fetchChangelog(): String {
    val log = rootProject.extensions.getByType<ChangelogPluginExtension>()
    return if (log.has(modVersion)) {
        log.renderItem(
            log.get(modVersion).withHeader(false),
            Changelog.OutputType.MARKDOWN
        )
    } else {
        ""
    }
}