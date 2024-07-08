plugins {
  kotlin("multiplatform") apply false
  id("com.android.library") apply false
  kotlin("plugin.serialization") apply false
  id("com.vanniktech.maven.publish") apply false
}

group = "xyz.mcxross.bcs"
version = "0.1.1"

repositories {
  mavenCentral()
}
