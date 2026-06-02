pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        // Huawei
        maven {
            url = uri("https://jitpack.io")
        }
        maven {
            url = uri("https://developer.huawei.com/repo/")
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        google()

        maven {
            url = uri("https://jitpack.io")
        }

        maven {
            url = uri("https://developer.huawei.com/repo/")
        }

        mavenCentral()
    }
}

rootProject.name = "New-Smarty-Kotline"
include(":app")
 