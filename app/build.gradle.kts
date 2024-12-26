plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    // KSP Annotation Processor plugin
    alias(libs.plugins.ksp)
    // Dagger-Hilt dependency injection library plugin
    alias(libs.plugins.dagger.hilt)
    // Room
     alias(libs.plugins.room)
    // Kotlin Serialization plugin
    alias(libs.plugins.kotlin.serialization)

    // Secrets Gradle Plugin
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.alexandros.p.gialamas.taxiapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.alexandros.p.gialamas.taxiapp"
        minSdk = 29
        targetSdk = 34
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

    // Room Schema
    room {
        schemaDirectory("$projectDir/schemas")
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    // GoogleMaps
    implementation("com.google.maps.android:maps-compose:4.3.3")

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.8.4")


    // Ktor
    implementation("io.ktor:ktor-client-core:3.0.0")
    implementation("io.ktor:ktor-client-android:3.0.0")
    implementation("io.ktor:ktor-client-content-negotiation:3.0.0")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.0.0")
    implementation ("io.ktor:ktor-client-serialization-jvm:3.0.0")
    implementation ("io.ktor:ktor-client-cio:3.0.0")
    implementation("io.ktor:ktor-client-logging:3.0.0")
    testImplementation("io.ktor:ktor-client-mock:3.0.0")

    // Gson
    implementation ("com.google.code.gson:gson:2.11.0")

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    ksp(libs.androidx.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // Timber Logging
    implementation(libs.timber)


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose.android)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.compose.material:material-icons-extended")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
    testImplementation("org.mockito:mockito-inline:4.11.0")
//    testImplementation("org.mockito:mockito-core:4.0.0")
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}