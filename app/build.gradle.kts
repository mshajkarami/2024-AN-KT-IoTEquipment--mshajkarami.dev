plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("androidx.navigation.safeargs")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.irisaco.iotequipment"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.irisaco.iotequipment"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        dataBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    // Gson Factory
    implementation("com.google.code.gson:gson:2.13.1")
    // Material
    implementation("com.google.android.material:material:1.9.0")
    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")
    // Crate Test Api
    implementation("com.squareup.okhttp3:mockwebserver:4.9.3")
    // Android core
    implementation("androidx.core:core:1.12.0")
    // BarCode Scanning -with Google Play Service
    implementation ("com.google.mlkit:barcode-scanning:17.3.0")
    // Camerax
    val camerax_version = "1.2.2"
    implementation ("androidx.camera:camera-core:${camerax_version}")
    implementation ("androidx.camera:camera-camera2:${camerax_version}")
    implementation ("androidx.camera:camera-lifecycle:${camerax_version}")
    implementation ("androidx.camera:camera-video:${camerax_version}")
    implementation ("androidx.camera:camera-view:${camerax_version}")
    implementation ("androidx.camera:camera-extensions:${camerax_version}")
    // Kotlinx Core
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-guava:1.7.3")
    // Room Database
    val room_version = "2.7.1"
    implementation("androidx.room:room-runtime:$room_version")
    ksp("androidx.room:room-compiler:$room_version")
    // AppAuth
//    implementation("net.openid:appauth:0.11.1")
    // CountdownView
    implementation("com.github.iwgang:countdownview:2.1.6")
    // Lotte
    implementation("com.airbnb.android:lottie:6.4.0")
    // MaterialDatePicker
    implementation("com.google.android.material:material:1.11.0")
}