// 启用类型安全的项目访问器功能
// 确保使用projects.core.model
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")


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
            url = uri("https://jitpack.io")
        }
    }
}

rootProject.name = "XinChat"
include(":app")
include(":core:designsystem")
include(":core:navigation")
include(":core:data")
include(":core:ui")
include(":feature:chat")
include(":feature:contact")
include(":feature:auth")
include(":feature:user")
include(":core:common")
include(":core:util")
include(":core:network")
include(":core:model")
include(":core:database")
include(":core:datastore")
include(":core:result")
include(":feature:main")
