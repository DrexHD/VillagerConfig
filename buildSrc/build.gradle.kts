plugins {
    `kotlin-dsl`
    kotlin("jvm") version "2.2.0"
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://maven.kikugie.dev/snapshots")
}

dependencies {
    fun plugin(id: String, version: String) = "$id:$id.gradle.plugin:$version"
    implementation(plugin("org.jetbrains.changelog", "2.2.0"))
    implementation(plugin("me.modmuss50.mod-publish-plugin", "1.1.0"))
    implementation("dev.kikugie:stonecutter:0.8-alpha.14")
}