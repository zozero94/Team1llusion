plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
    id("kotlinx-serialization")
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
}

android {
    namespace = "team.illusion"
    compileSdk = Versions.compileSdk

    defaultConfig {
        applicationId = "team.illusion"
        targetSdk = Versions.targetSdk
        versionCode = Versions.versionCode
        versionName = Versions.versionName
        minSdk = Versions.minSdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

    }

    buildTypes {
        release {
        }
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(project(":data"))
    implementation(platform(Dep.Firebase.bom))
    implementation(Dep.Firebase.analytics)
    implementation(Dep.Firebase.database)

    implementation(Dep.Android.appCompat)
    implementation(Dep.Android.startUp)
    implementation(Dep.Android.androidxCore)

    implementation(Dep.Lifecycle.viewModel)
    implementation(Dep.Lifecycle.composeViewModel)

    implementation(Dep.Compose.lifecycle)
    implementation(Dep.Compose.activity)
    implementation(Dep.Compose.ui)
    implementation(Dep.Compose.tooling)
    implementation(Dep.Compose.material)

    implementation(Dep.Kotlin.Coroutine.coroutineAndroid)
    implementation(Dep.Kotlin.Coroutine.coroutineCore)
    implementation(Dep.Kotlin.reflect)
    implementation(Dep.Square.retrofit)
    implementation(Dep.Square.okhttp3Logging)

    implementation(Dep.Square.timber)
    implementation(Dep.Square.serializationConverter)

    implementation(Dep.Kotlin.Serialization.serialization)
    kapt(Dep.Kotlin.Serialization.serializationPlugin)

    implementation(Dep.Dagger.android)
    kapt(Dep.Dagger.compiler)
    implementation(Dep.inject)
}