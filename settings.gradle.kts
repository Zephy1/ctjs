pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.fabricmc.net")
        maven("https://maven.architectury.dev")
        maven("https://maven.minecraftforge.net")
        maven("https://repo.essential.gg/repository/maven-public")
    }

    plugins {
        val egtVersion = "0.6.10"
        id("gg.essential.loom") version "1.9.+"
        id("gg.essential.multi-version.root") version egtVersion
    }
}

rootProject.name = "ctjs"
rootProject.buildFileName = "root.gradle.kts"

include(":typing-generator")
listOf(
//    "1.21.4-fabric",
    "1.21.5-fabric",
    "1.21.6-fabric",
    "1.21.7-fabric",
//    "1.21.8-fabric",
//    "1.21.9-fabric",
//    "1.21.10-fabric",
//    "1.21.11-fabric",
).forEach { version ->
    include(":$version")
    project(":$version").apply {
        projectDir = file("versions/$version")
        buildFileName = "../../build.gradle.kts"
    }
}
