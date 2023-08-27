import java.util.*

plugins {
  kotlin("multiplatform") version "1.9.0"
  kotlin("plugin.serialization") version "1.9.0"
  id("maven-publish")
  id("signing")
}

group = "xyz.mcxross.bcs"
version = "1.0.0"

ext["signing.keyId"] = null

ext["signing.password"] = null

ext["signing.secretKeyRingFile"] = null

ext["ossrhUsername"] = null

ext["ossrhPassword"] = null

repositories {
  mavenCentral()
}

kotlin {
  jvm {
    compilations.all {
      kotlinOptions.jvmTarget = "1.8"
    }
    withJava()
    testRuns["test"].executionTask.configure {
      useJUnitPlatform()
    }
  }
  js(IR) {
    browser {}
  }
  val hostOs = System.getProperty("os.name")
  val isMingwX64 = hostOs.startsWith("Windows")
  val nativeTarget = when {
    hostOs == "Mac OS X" -> macosX64("native")
    hostOs == "Linux" -> linuxX64("native")
    isMingwX64 -> mingwX64("native")
    else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
  }


  sourceSets {
    val commonMain by getting {
      dependencies {
        api("org.jetbrains.kotlinx:kotlinx-serialization-core:1.5.1")
      }
    }
    val commonTest by getting {
      dependencies {
        implementation(kotlin("test"))
      }
    }
    val jvmMain by getting
    val jvmTest by getting
    val jsMain by getting
    val jsTest by getting
    val nativeMain by getting
    val nativeTest by getting
  }
}

val secretPropsFile = project.rootProject.file("local.properties")

if (secretPropsFile.exists()) {
  secretPropsFile
    .reader()
    .use { Properties().apply { load(it) } }
    .onEach { (name, value) -> ext[name.toString()] = value }
} else {
  ext["signing.keyId"] = System.getenv("SIGNING_KEY_ID")
  ext["signing.password"] = System.getenv("SIGNING_PASSWORD")
  ext["signing.secretKeyRingFile"] = System.getenv("SIGNING_SECRET_KEY_RING_FILE")
  ext["ossrhUsername"] = System.getenv("OSSRH_USERNAME")
  ext["ossrhPassword"] = System.getenv("OSSRH_PASSWORD")
}

fun getExtraString(name: String) = ext[name]?.toString()

publishing {
  repositories {
    maven {
      name = "sonatype"
      setUrl("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
      credentials {
        username = getExtraString("ossrhUsername")
        password = getExtraString("ossrhPassword")
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
      scm { url.set("https://github.com/mcxross/kotlinx-serialization-bcs") }
    }
  }
}

signing { sign(publishing.publications) }
