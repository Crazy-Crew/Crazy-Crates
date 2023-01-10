rootProject.name = extra["name"] as String

dependencyResolutionManagement {
    includeBuild("build-logic")
}

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://papermc.io/repo/repository/maven-public/")
    }
}

include("paper", "common")