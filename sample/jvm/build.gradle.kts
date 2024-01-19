plugins {
  kotlin("jvm")
  kotlin("plugin.serialization")
  application
}

group = "xyz.mcxross.bcs.sample"

version = "0.1.0"

repositories { mavenCentral() }

kotlin { jvmToolchain(17) }

dependencies {
  implementation(project(":bcs"))
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.6.2")
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.getByName<Test>("test") { useJUnitPlatform() }

application { mainClass.set("xyz.mcxross.bcs.sample.MainKt") }
