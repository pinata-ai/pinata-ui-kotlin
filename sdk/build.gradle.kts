import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  id("com.android.library")
  id("org.jetbrains.kotlin.android")
  `maven-publish`
}

android {
  namespace = "com.pinata.ui"
  compileSdk = 36

  defaultConfig {
    minSdk = 24
    consumerProguardFiles("consumer-rules.pro")
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }

  publishing {
    singleVariant("release") { withSourcesJar() }
  }
}

kotlin.compilerOptions.jvmTarget.set(JvmTarget.JVM_17)

publishing {
  publications {
    register<MavenPublication>("release") {
      groupId = "ai.pinata"
      artifactId = "pinata-ui"
      version = property("sdkVersion") as String
      afterEvaluate { from(components["release"]) }
      pom {
        name.set("pinata-ui")
        description.set("Android SDK for the Pinata marketplace components")
        url.set("https://github.com/pinata-ai/pinata-ui-kotlin")
        licenses {
          license {
            name.set("MIT License")
            url.set("https://github.com/pinata-ai/pinata-ui-kotlin/blob/main/LICENSE")
          }
        }
        scm { url.set("https://github.com/pinata-ai/pinata-ui-kotlin") }
      }
    }
  }
  // CI uploads this folder to static.pinata.ai (see .github/workflows/publish.yml).
  repositories {
    maven {
      name = "local"
      url = uri(rootProject.layout.buildDirectory.dir("maven-repo"))
    }
  }
}

dependencies {
  implementation("androidx.browser:browser:1.8.0")
  implementation("androidx.webkit:webkit:1.8.0")
  testImplementation("junit:junit:4.13.2")
  // Real org.json for JVM tests; the android.jar stubs throw.
  testImplementation("org.json:json:20240303")
}
