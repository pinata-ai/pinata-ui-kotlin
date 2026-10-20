import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  id("com.android.library")
  id("org.jetbrains.kotlin.android")
}

android {
  namespace = "com.pinata.ui"
  compileSdk = 36

  defaultConfig {
    minSdk = 24
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
}

kotlin.compilerOptions.jvmTarget.set(JvmTarget.JVM_17)

dependencies {
  implementation("androidx.browser:browser:1.8.0")
  implementation("androidx.webkit:webkit:1.8.0")
}
