plugins {
  kotlin("jvm")
  kotlin("plugin.serialization") version "1.9.0"
  application
}

group = "xyz.mcxross.bcs.sample"
version = "0.1.0"

repositories { mavenCentral() }

kotlin {
  jvmToolchain(11)
}

dependencies {
  implementation(project(":bcs"))
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.getByName<Test>("test") { useJUnitPlatform() }

application {
  mainClass.set("xyz.mcxross.bcs.sample.MainKt")
}
