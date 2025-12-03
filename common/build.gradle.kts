plugins {
	id("multiloader-common")
	id("fabric-loom")
}

loom {
	accessWidenerPath = common.project.file("src/main/resources/villagerconfig.accesswidener")

	mixin {
		useLegacyMixinAp = false
	}
}

dependencies {
	minecraft("com.mojang:minecraft:$minecraftVersion")
	mappings(loom.officialMojangMappings())

    compileOnly("org.spongepowered:mixin:0.8.5")

    "io.github.llamalad7:mixinextras-common:0.3.5".let {
        compileOnly(it)
        annotationProcessor(it)
    }

    modCompileOnly("net.fabricmc:fabric-loader:${versionedProp("fabric-loader")}")
    modCompileOnly("me.zeroeightsix:fiber:${versionedProp("fiber")}")
    modCompileOnly("me.shedaniel.cloth:cloth-config-fabric:${versionedProp("cloth_config")}") {
        exclude("net.fabricmc.fabric-api")
    }
}

stonecutter {
    replacements.string(eval(current.version, "<=1.21.10")) {
        replace("Identifier", "ResourceLocation")
        replace("identifier()", "location()")
        replace("Lnet/minecraft/world/entity/npc/villager/", "Lnet/minecraft/world/entity/npc/")
        replace("net.minecraft.world.entity.npc.villager.", "net.minecraft.world.entity.npc.")
        replace("net.minecraft.world.entity.monster.zombie.", "net.minecraft.world.entity.monster.")
        replace("net.minecraft.advancements.criterion.", "net.minecraft.advancements.critereon.")
        replace("net.minecraft.util.Util", "net.minecraft.Util")
    }
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
