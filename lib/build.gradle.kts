plugins {
  kotlin("multiplatform")
  id("com.android.library")
  kotlin("plugin.serialization")
  id("maven-publish")
  id("signing")
}

group = "xyz.mcxross.bcs"
version = "1.0.0-SNAPSHOT"

repositories {
  mavenCentral()
  google()
}

kotlin {

  androidTarget {
    publishLibraryVariants("release", "debug")
  }

  ios()
  iosSimulatorArm64()

  js {
    browser()
    nodejs()
    compilations.all {
      kotlinOptions.sourceMap = true
      kotlinOptions.moduleKind = "umd"
    }
  }

  jvm {
    testRuns["test"].executionTask.configure {
      useJUnitPlatform()
    }
  }

  linuxX64()
  macosArm64()
  macosX64()
  mingwX64()
  tvosArm64()
  watchosArm64()

  sourceSets {
    val commonMain by getting {
      dependencies {
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.6.0")
      }
    }
    val commonTest by getting {
      dependencies {
        implementation(kotlin("test"))
      }
    }
    val iosMain by getting
    val iosSimulatorArm64Main by getting {
      dependsOn(iosMain)
    }
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
        setUrl("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
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
      description.set(
        "KMP implementation of the Binary Canonical Serialization (BCS) format",
      )
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
