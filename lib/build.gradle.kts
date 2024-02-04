plugins {
  kotlin("multiplatform")
  id("com.android.library")
  kotlin("plugin.serialization")
  id("maven-publish")
  id("signing")
}

group = "xyz.mcxross.bcs"

version = "1.0.1-SNAPSHOT"

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
  namespace = "mcxross.bcs"
  defaultConfig {
    minSdk = 24
    compileSdk = 33
  }
}

publishing {
  if (hasProperty("sonatypeUser") && hasProperty("sonatypePass")) {
    repositories {
      maven {
        name = "sonatype"
        val isSnapshot = version.toString().endsWith("-SNAPSHOT")
        setUrl(
          if (isSnapshot) {
            "https://s01.oss.sonatype.org/content/repositories/snapshots/"
          } else {
            "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
          }
        )
        credentials {
          username = property("sonatypeUser") as String
          password = property("sonatypePass") as String
        }
      }
    }
  }

  publications.withType<MavenPublication> {
    pom {
      name.set("BCS")
      description.set("KMP implementation of the Binary Canonical Serialization (BCS) format")
      url.set("https://github.com/mcxross")

      licenses {
        license {
          name.set("Apache License, Version 2.0")
          url.set("https://opensource.org/licenses/APACHE-2.0")
        }
      }
      developers {
        developer {
          id.set("mcxross")
          name.set("Mcxross")
          email.set("oss@mcxross.xyz")
        }
      }
      scm {
        connection.set("scm:git:git://github.com/mcxross/kotlinx-serialization-bcs.git")
        developerConnection.set("scm:git:ssh://github.com/mcxross/kotlinx-serialization-bcs.git")
        url.set("https://github.com/mcxross/kotlinx-serialization-bcs")
      }
    }
  }
}

signing {
  val sonatypeGpgKey = System.getenv("SONATYPE_GPG_KEY")
  val sonatypeGpgKeyPassword = System.getenv("SONATYPE_GPG_KEY_PASSWORD")
  when {
    sonatypeGpgKey == null || sonatypeGpgKeyPassword == null -> useGpgCmd()
    else -> useInMemoryPgpKeys(sonatypeGpgKey, sonatypeGpgKeyPassword)
  }
  sign(publishing.publications)
}
