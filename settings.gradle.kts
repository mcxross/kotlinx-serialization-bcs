pluginManagement {
  repositories {
    google()
    gradlePluginPortal()
    mavenCentral()
    mavenLocal()
  }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
  repositories {
    mavenCentral()
    google()
  }
}

rootProject.name = "kotlinx-serialization-bcs"
include(":lib", ":sample:jvm")
project(":lib").name = "bcs"
