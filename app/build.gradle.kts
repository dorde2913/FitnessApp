plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    id("com.google.dagger.hilt.android")
    //kotlin("kapt") // Required for annotation processing
    id("com.google.devtools.ksp")
    kotlin("kapt")
    kotlin("plugin.serialization") version "1.9.22"
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "com.example.fitnessapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.jimapp"
        minSdk = 30
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.play.services.wearable)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.compose.material)
    implementation(libs.compose.foundation)
    implementation(libs.wear.tooling.preview)
    implementation(libs.activity.compose)
    implementation(libs.core.splashscreen)
    implementation(libs.tiles)
    implementation(libs.tiles.material)
    implementation(libs.tiles.tooling.preview)
    implementation(libs.horologist.compose.tools)
    implementation(libs.horologist.tiles)
    implementation(libs.watchface.complications.data.source.ktx)
    implementation(libs.room.common)
    implementation(libs.room.ktx)
    implementation(libs.material3.android)
    implementation(libs.wear.ongoing)
    implementation(libs.compose.material3)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
    debugImplementation(libs.tiles.tooling)


    implementation(libs.health.services.client)



    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)

    // For Jetpack Compose integration
    implementation(libs.hilt.navigation.compose)

    // If using ViewModel with Hilt
    implementation(libs.lifecycle.viewmodel.ktx)

    implementation(libs.kotlinx.serialization.json) // Latest version


    implementation(libs.room.runtime) // Latest version
    annotationProcessor(libs.room.compiler) // For Java
    ksp(libs.room.compiler) // For Kotlin
    implementation(libs.room.ktx) // If using Kotlin Coroutines
    implementation(libs.wear)
    implementation(libs.accompanist.pager)


    implementation(libs.navigation.compose)

    //vico graphs
    implementation(libs.vico.compose)
    implementation(libs.vico.compose.m2)
    implementation(libs.vico.compose.m3)

    implementation(libs.datastore.preferences)

}