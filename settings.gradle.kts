pluginManagement {
    repositories {
        google {
            maven {
                url = uri("https://maven.myket.ir")
            }
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
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven {
            url = uri("https://maven.myket.ir")
        }
        maven {
            url = uri("https://jitpack.io")
        }

        google()
        mavenCentral()
    }
}

rootProject.name = "Iotequipment"
include(":app")
 