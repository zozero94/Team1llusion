plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
    id("kotlinx-serialization")
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
}

android {
    namespace = "team.illusion.data"
    compileSdk = 33

    defaultConfig {
        minSdk = Versions.minSdk
        compileSdk = Versions.compileSdk
        targetSdk = Versions.targetSdk

        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(Dep.Dagger.android)
    kapt(Dep.Dagger.compiler)
    implementation(Dep.inject)

    implementation(Dep.Android.dataStore)
    implementation(Dep.Android.dataStoreCore)

    implementation(platform(Dep.Firebase.bom))
    implementation(Dep.Firebase.analytics)
    implementation(Dep.Firebase.database)
    implementation(Dep.Firebase.auth)
    implementation(Dep.Firebase.authKtx)
    implementation(Dep.Firebase.authService)
    implementation(Dep.Square.retrofit)
    implementation(Dep.Square.okhttp3Logging)
    implementation(Dep.Square.timber)

    implementation(Dep.Kotlin.Serialization.serialization)
    kapt(Dep.Kotlin.Serialization.serializationPlugin)
    implementation(Dep.Square.serializationConverter)
}
