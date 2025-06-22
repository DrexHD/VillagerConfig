import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
	`multiloader-loader`
	id("net.neoforged.gradle.userdev") version "7.0.185"
	id("com.gradleup.shadow") version "8.3.6"
}

dependencies {
	implementation("net.neoforged:neoforge:${versionedProp("neoforge")}")
	implementation("me.zeroeightsix:fiber:${versionedProp("fiber")}")
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
		dependencies {
			runtime("me.zeroeightsix:fiber:${versionedProp("fiber")}")
		}
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

tasks.named<ShadowJar>("shadowJar") {
	archiveClassifier = ""
	dependencies {
		relocate("blue.endless.jankson", "me.drex.villagerconfig.shadow.jankson")
		relocate("io.github.fablabsmc", "me.drex.villagerconfig.shadow.fablabsmc")

		include(dependency("me.zeroeightsix:fiber"))
	}
	minimize()
}

tasks.jarJar {
	archiveClassifier = "dist"
	val shadowJar = tasks.shadowJar.get()
	dependsOn(shadowJar)
}