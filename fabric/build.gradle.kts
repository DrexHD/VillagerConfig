plugins {
    id("net.fabricmc.fabric-loom")
    `multiloader-loader`
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")

    implementation("net.fabricmc:fabric-loader:${versionedProp("fabric-loader")}")
    api("net.fabricmc.fabric-api:fabric-api:${versionedProp("fabric-api")}")

    compileOnly("com.terraformersmc:modmenu:${versionedProp("modmenu")}")
    implementation(include("blue.endless:jankson:${versionedProp("jankson")}")!!)
//    implementation("me.shedaniel.cloth:cloth-config-fabric:${versionedProp("cloth_config")}")
}

stonecutter {
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
        addToResources = false
        outputDirectory = file("vanilla")
    }
}

publishMods {
    file.set(tasks.jar.get().archiveFile)
}