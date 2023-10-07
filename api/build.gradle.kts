plugins {
    id("root-plugin")
}

project.group = "${rootProject.group}"
project.version = rootProject.version

dependencies {
    compileOnly(libs.adventure.api)
    compileOnly(libs.adventure.mm)

    compileOnly(libs.annotations)
}

val component: SoftwareComponent = components["java"]

tasks {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                artifactId = "api"

                from(component)

                pom {
                    name.set("CrazyCrates API")
                    description.set("A nifty crates plugin for Minecraft Servers.")
                    url.set("https://github.com/Crazy-Crew/CrazyCrates")

                    licenses {
                        license {
                            name.set("MIT")
                            url.set("https://github.com/Crazy-Crew/CrazyCrates/blob/main/LICENSE")
                        }
                    }

                    developers {
                        developer {
                            id.set("ryderbelserion")
                            name.set("Ryder Belserion")
                            url.set("https://github.com/ryderbelserion")
                            email.set("no-reply@ryderbelserion.com")
                        }

                        developer {
                            id.set("badbones69")
                            name.set("Badbones69")
                            url.set("https://github.com/badbones69")
                            email.set("joewojcik14@gmail.com")
                        }
                    }

                    scm {
                        connection.set("scm:git:https://github.com/Crazy-Crew/CrazyCrates.git")
                        developerConnection.set("scm:git:git@github.com:Crazy-Crew/CrazyCrates.git")
                        url.set("https://github.com/Crazy-Crew/CrazyCrates")
                    }

                    issueManagement {
                        system.set("GitHub")
                        url.set("https://github.com/Crazy-Crew/CrazyCrates/issues")
                    }
                }
            }
        }
    }
}