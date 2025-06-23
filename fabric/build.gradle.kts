plugins {
    id("fabric-loom")
    `multiloader-loader`
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings(loom.officialMojangMappings())

    modImplementation("net.fabricmc:fabric-loader:${versionedProp("fabric-loader")}")
    modApi("net.fabricmc.fabric-api:fabric-api:${versionedProp("fabric-api")}")

    modImplementation("com.terraformersmc:modmenu:${versionedProp("modmenu")}")
    implementation(include("me.zeroeightsix:fiber:${versionedProp("fiber")}")!!)
    modImplementation("me.shedaniel.cloth:cloth-config-fabric:${versionedProp("cloth_config")}")
}

loom {
    accessWidenerPath = common.project.file("src/main/resources/villagerconfig.accesswidener")

    runs {
        getByName("client") {
            client()
            configName = "Fabric Client"
            ideConfigGenerated(true)
        }
        getByName("server") {
            server()
            configName = "Fabric Server"
            ideConfigGenerated(true)
        }
    }

    mixin {
        defaultRefmapName = "villagerconfig.refmap.json"
    }
}

fabricApi {
    configureDataGeneration {
        client = true
        createRunConfiguration = true
        outputDirectory = file("vanilla")
    }
}

publishMods {
    file.set(tasks.remapJar.get().archiveFile)
}