plugins {
  alias(libs.plugins.android.kotlin.multiplatform.library) apply false
  alias(libs.plugins.dokka) apply false
  alias(libs.plugins.kotlin.multiplatform) apply false
  alias(libs.plugins.kotlin.serialization) apply false
  alias(libs.plugins.maven.publish) apply false
}

group = "xyz.mcxross.bcs"
version = "0.2.0"

repositories {
  mavenCentral()
}
