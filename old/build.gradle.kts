import io.papermc.hangarpublishplugin.model.Platforms

plugins {
    alias(libs.plugins.modrinth)
    alias(libs.plugins.hangar)

    id("paper-plugin")
}

project.group = "${rootProject.group}.paper"
project.version = rootProject.version

repositories {
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")

    maven("https://repo.aikar.co/content/groups/aikar/")

    maven("https://repo.triumphteam.dev/snapshots/")

    flatDir { dirs("libs") }
}

dependencies {
    implementation(project(":common"))

    implementation("org.bstats", "bstats-bukkit", "3.0.2")

    implementation("de.tr7zw", "item-nbt-api", "2.12.0")

    implementation(libs.cluster.bukkit.api) {
        exclude("com.ryderbelserion.cluster", "cluster-api")
    }

    implementation("dev.triumphteam", "triumph-cmd-bukkit", "2.0.0-SNAPSHOT")

    implementation("dev.triumphteam", "triumph-gui", "3.1.2")

    compileOnly(fileTree("libs").include("*.jar"))

    compileOnly("me.filoghost.holographicdisplays", "holographicdisplays-api", "3.0.0")

    compileOnly("com.github.decentsoftware-eu", "decentholograms","2.8.4")

    compileOnly("com.github.LoneDev6", "API-ItemsAdder", "3.5.0b")

    compileOnly("com.github.oraxen", "oraxen", "1.160.0") {
        exclude("*", "*")
    }

    compileOnly("me.clip", "placeholderapi", "2.11.4")
}

tasks {
    shadowJar {
        listOf(
            "de.tr7zw",
            "org.bstats",
            "dev.triumphteam.gui",
            "dev.triumphteam.cmd"
        ).forEach {
            relocate(it, "libs.$it")
        }
    }

    processResources {
        val props = mapOf(
            "name" to rootProject.name,
            "group" to project.group,
            "version" to rootProject.version,
            "description" to rootProject.description,
            "authors" to rootProject.properties["authors"],
            "apiVersion" to rootProject.properties["apiVersion"],
            "website" to rootProject.properties["website"],
        )

        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}

val isSnapshot = true
val type = if (isSnapshot) "beta" else "release"
val other = if (isSnapshot) "Beta" else "Release"

val file = file("${rootProject.rootDir}/jars/${rootProject.name}-${rootProject.version}.jar")

val description = """
## Changes:
 * N/A

## New Features:
 * N/A
    
## Other:
 * [Feature Requests](https://github.com/Crazy-Crew/${rootProject.name}/issues)
 * [Bug Reports](https://github.com/Crazy-Crew/${rootProject.name}/issues)
""".trimIndent()

val versions = listOf(
    "1.20",
    "1.20.1",
    "1.20.2"
)

modrinth {
    autoAddDependsOn.set(false)

    token.set(System.getenv("MODRINTH_TOKEN"))

    projectId.set(rootProject.name.lowercase())

    versionName.set("${rootProject.name} ${rootProject.version}")
    versionNumber.set("${rootProject.version}")

    versionType.set(type)

    uploadFile.set(file)

    gameVersions.addAll(versions)

    changelog.set(description)

    loaders.addAll("paper", "purpur")
}

hangarPublish {
    publications.register("plugin") {
        version.set(rootProject.version as String)

        id.set(rootProject.name)

        channel.set(if (isSnapshot) "Beta" else "Release")

        changelog.set(description)

        apiKey.set(System.getenv("hangar_key"))

        platforms {
            register(Platforms.PAPER) {
                jar.set(file)
                platformVersions.set(versions)
            }
        }
    }
}