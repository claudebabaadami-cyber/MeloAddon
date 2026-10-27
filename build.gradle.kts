plugins {
    alias(libs.plugins.fabric.loom)
}

base {
    archivesName = providers.gradleProperty("archives_base_name").get()
    version = libs.versions.mod.version.get()
    group = providers.gradleProperty("maven_group").get()
}

repositories {
    maven { url = uri("https://maven.meteordev.org/releases") }
    maven { url = uri("https://maven.meteordev.org/snapshots") }
    maven { url = uri("https://maven.fabricmc.net/") }
    mavenCentral()
}

dependencies {
    minecraft(libs.minecraft)
    modImplementation(libs.fabric.loader)
    modImplementation(libs.meteor.client)
}

java {
    toolchain { languageVersion.set(JavaLanguageVersion.of(21)) }
    withSourcesJar()
}

tasks.processResources {
    val properties = mapOf(
        "version" to project.version,
        "minecraft_version" to libs.versions.minecraft.get(),
        "jdk_version" to libs.versions.jdk.get()
    )
    inputs.properties(properties)
    filesMatching("fabric.mod.json") { expand(properties) }
}
