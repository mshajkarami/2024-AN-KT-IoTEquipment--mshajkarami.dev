plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("androidx.navigation.safeargs")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
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

        manifestPlaceholders["appAuthRedirectScheme"] = "com.irisaco.iotequipment"

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
        viewBinding = true
    }
}

dependencies {

    // =================== Core AndroidX Libraries ===================
    implementation(libs.androidx.core.ktx) // Kotlin core extensions for Android
    implementation(libs.androidx.appcompat) // AppCompat backward compatibility
    implementation(libs.androidx.activity) // Support for Activity APIs
    implementation(libs.androidx.constraintlayout) // ConstraintLayout for flexible UI
    implementation(libs.androidx.swiperefreshlayout) // Pull to refresh

    // =================== UI Components ===================
    implementation(libs.material) // Google's Material Design components (versioned in libs.versions.toml)
    implementation("com.google.android.material:material:1.11.0") // Newer Material Components (can unify version later)
    implementation("com.facebook.shimmer:shimmer:0.5.0") // Skeleton loading UI (Shimmer effect)
    implementation("com.airbnb.android:lottie:6.4.0") // Lottie animation support
    implementation("com.github.iwgang:countdownview:2.1.6") // Countdown timer view

    // =================== Navigation ===================
    implementation(libs.androidx.navigation.fragment.ktx) // Navigation component: fragment support
    implementation(libs.androidx.navigation.ui.ktx) // Navigation component: UI integration

    // =================== Networking ===================
    implementation("com.google.code.gson:gson:2.13.1") // JSON serialization/deserialization
    implementation("com.squareup.retrofit2:retrofit:3.0.0") // Retrofit core
    implementation("com.squareup.retrofit2:converter-gson:2.9.0") // Gson converter for Retrofit
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0") // Scalars (String, etc.) converter
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3") // Logs HTTP requests/responses
    implementation("com.squareup.okhttp3:mockwebserver:4.9.3") // Mock server for testing API calls

    // =================== CameraX & ML Kit ===================
    val camerax_version = "1.2.2"
    implementation("androidx.camera:camera-core:$camerax_version")
    implementation("androidx.camera:camera-camera2:$camerax_version")
    implementation("androidx.camera:camera-lifecycle:$camerax_version")
    implementation("androidx.camera:camera-video:$camerax_version")
    implementation("androidx.camera:camera-view:$camerax_version")
    implementation("androidx.camera:camera-extensions:$camerax_version")

    implementation("com.google.mlkit:barcode-scanning:17.3.0") // ML Kit for barcode scanning

    // =================== KotlinX & Coroutines ===================
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-guava:1.7.3") // Coroutine support for Guava

    // =================== Room Database ===================
    val room_version = "2.7.1"
    implementation("androidx.room:room-runtime:$room_version") // Room ORM for SQLite
    ksp("androidx.room:room-compiler:$room_version") // Annotation processor for Room

    // =================== AppAuth (OAuth/OpenID) ===================
    implementation("net.openid:appauth:0.11.1") // Authentication using OAuth 2.0 and OpenID Connect

    // =================== Dependency Injection (Hilt) ===================
    implementation("com.google.dagger:hilt-android:2.56.2") // Hilt for DI
    ksp("com.google.dagger:hilt-android-compiler:2.56.2") // Hilt annotation processor

    // =================== Data Persistence ===================
    implementation("androidx.datastore:datastore-preferences:1.0.0") // Modern data storage (replacement for SharedPreferences)
    implementation("androidx.security:security-crypto:1.1.0-alpha06") // Security data save (replacement for SharedPreferences)

    // =================== Image Loading ===================
    implementation("io.coil-kt:coil:2.5.0") // Image loading and caching library for Android

    // =================== Testing ===================
    testImplementation(libs.junit) // Unit testing
    androidTestImplementation(libs.androidx.junit) // Android JUnit support
    androidTestImplementation(libs.androidx.espresso.core) // UI testing with Espresso
}
