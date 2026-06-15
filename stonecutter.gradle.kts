plugins {
    id("dev.kikugie.stonecutter")
    id("org.jetbrains.changelog")
    id("net.fabricmc.fabric-loom") version "1.16-SNAPSHOT" apply false
}

stonecutter active "26.2"

changelog {
    path = rootProject.file("CHANGELOG.md").path
}