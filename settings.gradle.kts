import org.gradle.internal.impldep.org.bouncycastle.oer.its.etsi102941.Url

pluginManagement {
    repositories {
        google()
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        // Huawei
        maven {
            url = uri("https://jitpack.io")
        }
        maven {
            url = uri("https://developer.huawei.com/repo/")
        }

    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://jitpack.io")
        }

        maven {
            url = uri("https://developer.huawei.com/repo/")
        }


    }
}

rootProject.name = "New-Smarty-Kotline"
include(":app")
 