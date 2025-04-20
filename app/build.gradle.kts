
plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("org.jetbrains.kotlin.plugin.compose")
    id("dagger.hilt.android.plugin")
    id("kotlinx-serialization")
    id("kotlin-parcelize")

}
val gitCommitHash = "git rev-parse --short HEAD".execute().trim()
val gitCommitMessage = "git log --format=%s -n 1".execute().trim()

fun String.execute(): String {
    val process = ProcessBuilder(split("\\s".toRegex())).redirectErrorStream(true).start()
    return process.inputStream.bufferedReader().readText()
}
kotlin {
    compilerOptions {
        languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_1)
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_1)
    }
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

        buildConfigField("String", "GIT_COMMIT_HASH", "\"$gitCommitHash\"")
        buildConfigField("String", "GIT_COMMIT_MESSAGE", "\"$gitCommitMessage\"")
    }

    buildTypes {
        release {
        }
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(project(":data"))

    implementation(Dep.Android.appCompat)
    implementation(Dep.Android.startUp)
    implementation(Dep.Android.androidxCore)

    implementation(Dep.Firebase.auth)
    implementation(Dep.Firebase.authService)

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

    implementation(Dep.Square.timber)
    implementation(Dep.Square.serializationConverter)

    implementation(Dep.Kotlin.Serialization.serialization)
    kapt(Dep.Kotlin.Serialization.serializationPlugin)

    implementation(Dep.Dagger.android)
    kapt(Dep.Dagger.compiler)
    implementation(Dep.inject)
    implementation(Dep.Compose.icons_core)
    implementation(Dep.Compose.icons_extended)
}
