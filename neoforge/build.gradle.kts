plugins {
	`multiloader-loader`
	id("net.neoforged.moddev")
}

neoForge {
	enable {
		version = versionedProp("neoforge")
	}
}

val additionalRuntimeClasspath = configurations.getByName("additionalRuntimeClasspath")

dependencies {
	implementation(jarJar("me.zeroeightsix:fiber:${versionedProp("fiber")}")!!)
	additionalRuntimeClasspath("me.zeroeightsix:fiber:${versionedProp("fiber")}")
	implementation("me.shedaniel.cloth:cloth-config-neoforge:${versionedProp("cloth_config")}")
}

neoForge {
	accessTransformers.from(project.file("src/main/resources/META-INF/accesstransformer.cfg").absolutePath)

	runs {
		register("client") {
			client()
			ideName = "NeoForge Client (${path})"
		}
		register("server") {
			server()
			ideName = "NeoForge Server (${path})"
		}
	}

	mods {
		register("villagerconfig") {
			sourceSet(sourceSets.main.get())
		}
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