import java.security.MessageDigest
import java.util.Properties

plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
  id("com.google.dagger.hilt.android")
  id("com.google.devtools.ksp")
  id("org.jetbrains.kotlin.plugin.compose")
  kotlin("plugin.serialization")
  id("com.google.gms.google-services")
  id("org.jlleitschuh.gradle.ktlint")
  id("io.gitlab.arturbosch.detekt")
}

detekt {
  buildUponDefaultConfig = true
  allRules = false
  config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
}

android {
  namespace = "git.shin.animevsub"
  compileSdk = 36

  val localProperties = Properties()
  val localPropertiesFile = rootProject.file("local.properties")
  if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
  }

  fun sha256(input: String): String {
    val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
  }

  val devPassword = localProperties.getProperty("PASSWORD_UNLOCK_DEVELOPER").toString()
  val hashedDevPassword = sha256(devPassword)

  defaultConfig {
    applicationId = "git.shin.animevsub"
    minSdk = 26
    targetSdk = 36
    versionCode = project.property("versionCode").toString().toInt()
    versionName = project.property("versionName").toString()

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
    buildConfigField(
      "String",
      "DEV_PWD_HASH",
      "\"$hashedDevPassword\""
    )
    buildConfigField(
      "String",
      "RELEASE_CERT_SHA256",
      "\"${localProperties.getProperty("RELEASE_CERT_SHA256") ?: ""}\""
    )

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    vectorDrawables {
      useSupportLibrary = true
    }
  }

  buildTypes {
    debug {
      applicationIdSuffix = ".dev"
      versionNameSuffix = "-dev"
    }
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
    jniLibs {
      useLegacyPackaging = true
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
  implementation("androidx.compose.material3:material3-window-size-class")
  implementation("androidx.compose.material3.adaptive:adaptive:1.2.0")
  implementation("androidx.compose.material3.adaptive:adaptive-layout:1.2.0")
  implementation("androidx.compose.material3.adaptive:adaptive-navigation:1.2.0")
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
  ksp("com.google.dagger:hilt-android-compiler:2.54")
  implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
  implementation("androidx.hilt:hilt-work:1.3.0")
  ksp("androidx.hilt:hilt-compiler:1.3.0")

  // Room
  val roomVersion = "2.6.1"
  implementation("androidx.room:room-runtime:$roomVersion")
  implementation("androidx.room:room-ktx:$roomVersion")
  ksp("androidx.room:room-compiler:$roomVersion")

  // WorkManager
  implementation("androidx.work:work-runtime-ktx:2.11.2")

  // Network
  implementation("com.squareup.okhttp3:okhttp:5.3.2")

  // HTML Parsing (replaces cheerio)
  implementation("org.jsoup:jsoup:1.22.1")

  // Fix R8 missing classes
  implementation("com.google.re2j:re2j:1.7")
  implementation("org.slf4j:slf4j-nop:2.0.13")

  // Image Loading
  implementation("io.coil-kt:coil-compose:2.7.0")
  implementation("io.coil-kt:coil-gif:2.7.0")

  // DataStore
  implementation("androidx.datastore:datastore-preferences:1.2.1")

  // Media3 ExoPlayer
  implementation("androidx.media3:media3-exoplayer:1.10.0")
  implementation("androidx.media3:media3-exoplayer-hls:1.10.0")
  implementation("androidx.media3:media3-ui:1.10.0")
  implementation("androidx.media3:media3-cast:1.10.0")
  implementation("com.google.android.gms:play-services-cast-framework:22.3.1")
  implementation("androidx.mediarouter:mediarouter:1.7.0")
  implementation("androidx.appcompat:appcompat:1.7.0")

  // Supabase
  implementation("io.github.jan-tennert.supabase:postgrest-kt:2.6.1")
  implementation("io.github.jan-tennert.supabase:gotrue-kt:2.6.1")
  implementation("io.ktor:ktor-client-okhttp:2.3.13")

  // JSON
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

  // Firebase
  implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
  implementation("com.google.firebase:firebase-analytics")

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

  // Markdown
  implementation("com.github.jeziellago:compose-markdown:0.7.1")

  // FFmpeg (Mobile FFmpeg - Older but stable)
  implementation("com.arthenica:mobile-ffmpeg-full:4.4")

  // Testing
  testImplementation("junit:junit:4.13.2")
  androidTestImplementation("androidx.test.ext:junit:1.3.0")
  debugImplementation("androidx.compose.ui:ui-tooling")
  debugImplementation("androidx.compose.ui:ui-test-manifest")
}
