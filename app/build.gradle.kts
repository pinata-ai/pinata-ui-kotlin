import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
}

android {
  namespace = "com.pinata.ui.demo"
  compileSdk = 36

  defaultConfig {
    applicationId = "com.pinata.ui.demo"
    minSdk = 24
    targetSdk = 36
    versionCode = 1
    versionName = "0.1.0"
    buildConfigField("String", "DEMO_TOKEN", "\"${property("demoToken")}\"")
    buildConfigField("String", "DEMO_ENV", "\"${property("demoEnv")}\"")
  }

  buildFeatures.buildConfig = true

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
}

kotlin.compilerOptions.jvmTarget.set(JvmTarget.JVM_17)

dependencies {
  implementation(project(":sdk"))
  implementation("androidx.activity:activity-ktx:1.10.1")
  implementation("androidx.core:core-ktx:1.13.1")
}
