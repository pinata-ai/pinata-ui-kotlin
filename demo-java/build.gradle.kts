plugins {
  id("com.android.application")
}

android {
  namespace = "com.pinata.ui.demojava"
  compileSdk = 36

  defaultConfig {
    applicationId = "com.pinata.ui.demojava"
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

dependencies {
  implementation(project(":sdk"))
}
