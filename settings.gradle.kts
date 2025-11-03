pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.fabricmc.net")
        maven("https://maven.architectury.dev")
        maven("https://maven.minecraftforge.net")
        maven("https://repo.essential.gg/repository/maven-public")
        maven("https://repo.legacyfabric.net/repository/legacyfabric/")
    }
}

// !! This uses my own fork of the toolkit, I couldn't get 1.21.9+ to build on the maven build (I couldn't update past Loom 1.9.x due to depreciated methods) !!
includeBuild("../essential-gradle-toolkit")
rootProject.name = "ctjs"
rootProject.buildFileName = "root.gradle.kts"

include(":typing-generator")
listOf(
    "1.21.5-fabric",
    "1.21.8-fabric",
    "1.21.10-fabric",
//    "1.21.11-fabric",
).forEach { version ->
    include(":$version")
    project(":$version").apply {
        projectDir = file("versions/$version")
        buildFileName = "../../build.gradle.kts"
    }
}
