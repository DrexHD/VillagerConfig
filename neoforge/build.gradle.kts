plugins {
	`multiloader-loader`
	id("net.neoforged.gradle.userdev") version "7.1.21"
}

dependencies {
	implementation("net.neoforged:neoforge:${versionedProp("neoforge")}")
	implementation(jarJar("blue.endless:jankson:${versionedProp("jankson")}")!!)
	implementation("me.shedaniel.cloth:cloth-config-neoforge:${versionedProp("cloth_config")}")
}

minecraft {
	accessTransformers {
		file(project.file("src/main/resources/META-INF/accesstransformer.cfg").absolutePath)
	}
}

runs {
	named("client") {
		ideRunName = "NeoForge Client (${path})"
	}
	named("server") {
		ideRunName = "NeoForge Server (${path})"
	}

	configureEach {
		modSource(project.sourceSets.main.get())
		workingDirectory("run")
	}
}

sourceSets.main {
	resources.srcDir("src/generated/resources")
}

tasks {
	processResources {
		exclude("villagerconfig.accesswidener")
	}
}

publishMods {
	file.set(tasks.jarJar.get().archiveFile)
}