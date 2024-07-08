import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import com.vanniktech.maven.publish.SonatypeHost

plugins {
  kotlin("multiplatform")
  id("com.android.library")
  kotlin("plugin.serialization")
  id("org.jetbrains.dokka") version "1.9.20"
  id("com.vanniktech.maven.publish")
}

group = "xyz.mcxross.bcs"

version = "0.1.1"

repositories {
  mavenCentral()
  google()
}

kotlin {
  androidTarget { publishLibraryVariants("release", "debug") }

  iosX64()
  iosArm64()
  iosSimulatorArm64()

  js {
    browser()
    nodejs()
    compilations.all {
      kotlinOptions.sourceMap = true
      kotlinOptions.moduleKind = "umd"
    }
  }

  jvm { testRuns["test"].executionTask.configure { useJUnitPlatform() } }

  mingwX64()
  linuxX64()
  linuxArm64()
  macosArm64()
  macosX64()
  tvosArm64()
  tvosX64()
  tvosSimulatorArm64()
  watchosArm32()
  watchosArm64()
  watchosX64()
  watchosSimulatorArm64()

  sourceSets {
    commonMain.dependencies {
      implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.6.2")
    }
    commonTest.dependencies { implementation(kotlin("test")) }
  }
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))

android {
  namespace = "xyz.mcxross.bcs"
  defaultConfig {
    minSdk = 24
    compileSdk = 33
  }
}

mavenPublishing {
  coordinates("xyz.mcxross.bcs", "bcs", version.toString())

  configure(
    KotlinMultiplatform(
      javadocJar = JavadocJar.Dokka("dokkaHtml"),
      sourcesJar = true,
      androidVariantsToPublish = listOf("debug", "release"),
    )
  )

  pom {
    name.set("BCS")
    description.set("KMP implementation of the Binary Canonical Serialization (BCS) format")
    inceptionYear.set("2023")
    url.set("https://github.com/mcxross")
    licenses {
      license {
        name.set("The Apache License, Version 2.0")
        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
        distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
      }
    }
    developers {
      developer {
        id.set("mcxross")
        name.set("Mcxross")
        email.set("oss@mcxross.xyz")
        url.set("https://mcxross.xyz/")
      }
    }
    scm {
      url.set("https://github.com/mcxross/kotlinx-serialization-bcs")
      connection.set("scm:git:ssh://github.com/mcxross/kotlinx-serialization-bcs.git")
      developerConnection.set("scm:git:ssh://github.com/mcxross/kotlinx-serialization-bcs.git")
    }
  }

  publishToMavenCentral(SonatypeHost.S01, automaticRelease = true)

  signAllPublications()
}
