pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()

        maven {
            url = uri(
                "https://maven.pkg.github.com/Akiha678/Android_Widget"
            )

            credentials {
                username = System.getenv("GITHUB_USERNAME")

                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

rootProject.name = "XinChat"
include(":app")
include(":core:designsystem")
include(":core:ui")
include(":feature:chat")
include(":feature:auth")
include(":feature:user")
include(":core:navigation")
