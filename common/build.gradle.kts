plugins {
	id("multiloader-common")
	id("net.fabricmc.fabric-loom")
}

loom {
	accessWidenerPath = common.project.file("src/main/resources/villagerconfig.accesswidener")

	mixin {
		useLegacyMixinAp = false
	}
}

dependencies {
	minecraft("com.mojang:minecraft:$minecraftVersion")

    compileOnly("org.spongepowered:mixin:0.8.5")

    "io.github.llamalad7:mixinextras-common:0.5.0".let {
        compileOnly(it)
        annotationProcessor(it)
    }

    compileOnly("net.fabricmc:fabric-loader:${versionedProp("fabric-loader")}")
	compileOnly("blue.endless:jankson:${versionedProp("jankson")}")
//    compileOnly("me.shedaniel.cloth:cloth-config-fabric:${versionedProp("cloth_config")}") {
//        exclude("net.fabricmc.fabric-api")
//    }
}

stonecutter {
}

val commonJava: Configuration by configurations.creating {
	isCanBeResolved = false
	isCanBeConsumed = true
}

val commonResources: Configuration by configurations.creating {
	isCanBeResolved = false
	isCanBeConsumed = true
}

artifacts {
	afterEvaluate {
		val mainSourceSet = sourceSets.main.get()
		mainSourceSet.java.sourceDirectories.files.forEach {
			add(commonJava.name, it)
		}
		mainSourceSet.resources.sourceDirectories.files.forEach {
			add(commonResources.name, it)
		}
	}
}
