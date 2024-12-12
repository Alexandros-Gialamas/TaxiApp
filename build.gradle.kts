// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    dependencies {
        classpath("com.google.android.libraries.mapsplatform.secrets-gradle-plugin:secrets-gradle-plugin:2.0.1")
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false

    // KSP Annotation Processor plugin
    alias(libs.plugins.ksp) apply false
    // Dagger-Hilt dependency injection library plugin
    alias(libs.plugins.dagger.hilt) apply false
    // Kotlin Serialization plugin
    alias(libs.plugins.kotlin.serialization) apply false
}