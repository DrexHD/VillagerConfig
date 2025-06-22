import dev.kikugie.stonecutter.RunConfigType

plugins {
    id("dev.kikugie.stonecutter")
    id("org.jetbrains.changelog")
    id("fabric-loom") version "1.10-SNAPSHOT" apply false
}

stonecutter active "1.21.6"

stonecutter registerChiseled tasks.register("chiseledBuild", stonecutter.chiseled) {
	group = "project"
	ofTask("build")
}

stonecutter registerChiseled tasks.register("chiseledPublishMods", stonecutter.chiseled) {
    group = "project"
    ofTask("publishMods")
}

stonecutter registerChiseled tasks.register("chiseledRunDatagen", stonecutter.chiseled) {
    group = "project"
    ofTask("runDatagen")
}

stonecutter {
	generateRunConfigs = listOf(RunConfigType.SWITCH)
}

changelog {
    path = rootProject.file("CHANGELOG.md").path
}