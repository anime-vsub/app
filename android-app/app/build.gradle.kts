import java.util.Properties

plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
  id("com.google.dagger.hilt.android")
  kotlin("kapt")
  id("org.jetbrains.kotlin.plugin.compose")
  kotlin("plugin.serialization")
}

android {
  namespace = "git.shin.animevsub"
  compileSdk = 36

  val localProperties = Properties()
  val localPropertiesFile = rootProject.file("local.properties")
  if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
  }

  defaultConfig {
    applicationId = "git.shin.animevsub"
    minSdk = 26
    targetSdk = 36
    versionCode = 1
    versionName = "2.0.0"

    buildConfigField(
      "String",
      "SUPABASE_URL",
      "\"${localProperties.getProperty("SUPABASE_URL") ?: ""}\""
    )
    buildConfigField(
      "String",
      "SUPABASE_KEY",
      "\"${localProperties.getProperty("SUPABASE_KEY") ?: ""}\""
    )

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    vectorDrawables {
      useSupportLibrary = true
    }
  }

  buildTypes {
    release {
      isMinifyEnabled = true
      isShrinkResources = true
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro"
      )
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
  }
  kotlinOptions {
    jvmTarget = "21"
  }
  buildFeatures {
    compose = true
    buildConfig = true
  }
  packaging {
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
  }
}

dependencies {
  // Compose BOM
  val composeBom = platform("androidx.compose:compose-bom:2024.12.01")
  implementation(composeBom)

  // Compose
  implementation("androidx.compose.ui:ui")
  implementation("androidx.compose.ui:ui-graphics")
  implementation("androidx.compose.ui:ui-tooling-preview")
  implementation("androidx.compose.material3:material3")
  implementation("androidx.compose.material:material-icons-extended")
  implementation("androidx.compose.foundation:foundation")

  // Activity & Lifecycle
  implementation("androidx.activity:activity-compose:1.10.1")
  implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
  implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")

  // Navigation
  implementation("androidx.navigation:navigation-compose:2.8.8")

  // Hilt
  implementation("com.google.dagger:hilt-android:2.54")
  kapt("com.google.dagger:hilt-android-compiler:2.54")
  kapt("org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.9.0")
  implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

  // Network
  implementation("com.squareup.okhttp3:okhttp:5.3.2")

  // HTML Parsing (replaces cheerio)
  implementation("org.jsoup:jsoup:1.22.1")

  // Image Loading
  implementation("io.coil-kt:coil-compose:2.7.0")

  // DataStore
  implementation("androidx.datastore:datastore-preferences:1.2.1")

    // Media3 ExoPlayer
    implementation("androidx.media3:media3-exoplayer:1.10.0")
    implementation("androidx.media3:media3-exoplayer-hls:1.10.0")
    implementation("androidx.media3:media3-ui:1.10.0")

  // Supabase
  implementation("io.github.jan-tennert.supabase:postgrest-kt:2.6.1")
  implementation("io.github.jan-tennert.supabase:gotrue-kt:2.6.1")
  implementation("io.ktor:ktor-client-okhttp:2.3.13")

  // JSON
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

  // Coroutines
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")

  // Accompanist
  implementation("com.google.accompanist:accompanist-systemuicontroller:0.36.0")
  implementation("com.google.accompanist:accompanist-pager:0.36.0")
  implementation("com.google.accompanist:accompanist-pager-indicators:0.36.0")
  implementation("com.google.accompanist:accompanist-swiperefresh:0.36.0")
  implementation("com.google.accompanist:accompanist-placeholder-material:0.36.0")

  // Core
  implementation("androidx.core:core-ktx:1.15.0")

  // Testing
  testImplementation("junit:junit:4.13.2")
  androidTestImplementation("androidx.test.ext:junit:1.3.0")
  debugImplementation("androidx.compose.ui:ui-tooling")
  debugImplementation("androidx.compose.ui:ui-test-manifest")
}

kapt {
  correctErrorTypes = true
}
