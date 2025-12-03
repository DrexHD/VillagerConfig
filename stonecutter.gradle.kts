plugins {
    id("dev.kikugie.stonecutter")
    id("org.jetbrains.changelog")
    id("fabric-loom") version "1.13-SNAPSHOT" apply false
}

stonecutter active "1.21.11"

changelog {
    path = rootProject.file("CHANGELOG.md").path
}