plugins {
  kotlin("multiplatform") apply false
  id("com.android.library") apply false
  kotlin("plugin.serialization") apply false
}

group = "xyz.mcxross.bcs"
version = "1.0.0-SNAPSHOT"

repositories {
  mavenCentral()
}
