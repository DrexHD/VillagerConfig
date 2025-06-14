import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.ChangelogPluginExtension

plugins {
    id("fabric-loom") version "1.10-SNAPSHOT"
    id("maven-publish")
    id("me.modmuss50.mod-publish-plugin") version "0.8.4"
}

val baseArchiveName = project.property("archives_base_name") as String
val modVersion = project.property("mod_version") as String
val minecraftVersion = project.property("minecraft_version") as String
val mavenGroup = project.property("maven_group") as String
val loaderVersion = project.property("loader_version") as String
val fabricVersion = project.property("fabric_version") as String
val modMenuVersion = project.property("mod_menu_version") as String
val clothConfigVersion = project.property("cloth_config_version") as String
val fiberVersion = project.property("fiber_version") as String
val curseforgeMinecraftVersion = project.property("curseforge_minecraft_version") as String

base {
    archivesName = baseArchiveName
}

version = "$modVersion+$minecraftVersion"
group = mavenGroup

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

repositories {
    mavenCentral()
    maven("https://maven.shedaniel.me/")
    maven("https://maven.terraformersmc.com/releases/")
    maven("https://jitpack.io")
}

loom {
    accessWidenerPath.set(file("src/main/resources/villagerconfig.accesswidener"))
    runConfigs.all {
        ideConfigGenerated(true)
    }
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:$loaderVersion")
    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabricVersion")
    modCompileOnly("com.terraformersmc:modmenu:$modMenuVersion")
    modCompileOnly("me.shedaniel.cloth:cloth-config-fabric:$clothConfigVersion")
    modImplementation("me.zeroeightsix:fiber:$fiberVersion")
    include("me.zeroeightsix:fiber:$fiberVersion")
}

fabricApi {
    configureDataGeneration {
        client = true
        outputDirectory = file("vanilla")
    }
}

tasks.named<ProcessResources>("processResources") {
    inputs.property("version", project.version)
    filesMatching("fabric.mod.json") {
        expand("version" to project.version)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(21)
}

publishMods {
    file.set(tasks.remapJar.get().archiveFile)
    type.set(STABLE)
    changelog.set(fetchChangelog())

    displayName.set("VillagerConfig ${version}")
    modLoaders.add("fabric")
    modLoaders.add("quilt")

    curseforge {
        accessToken.set(providers.environmentVariable("CURSEFORGE_TOKEN"))
        projectId.set("400741")
        minecraftVersions.add(curseforgeMinecraftVersion)
    }

    modrinth {
        accessToken.set(providers.environmentVariable("MODRINTH_TOKEN"))
        projectId.set("OClpEDe3")
        minecraftVersions.add(minecraftVersion)
    }

    github {
        accessToken.set(providers.environmentVariable("GITHUB_TOKEN"))
        repository.set(providers.environmentVariable("GITHUB_REPOSITORY").orElse("DrexHD/VillagerConfig"))
        commitish.set(providers.environmentVariable("GITHUB_REF_NAME").orElse("main"))
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
