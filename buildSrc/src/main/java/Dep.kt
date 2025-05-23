object Versions {
    const val compileSdk = 35

    const val minSdk = 26
    const val targetSdk = 34
    const val versionCode = 1
    const val versionName = "1.0.0"
}

object Dep {
    const val androidGradlePlugin = "com.android.tools.build:gradle:8.8.2"
    const val startUp = "androidx.startup:startup-runtime:1.1.0"

    object Android {
        const val androidxCore = "androidx.core:core-ktx:1.16.0"
        //todo 최신은 1.10.1 preview 문제로 잠시 다운그레이드
        const val appCompat = "androidx.appcompat:appcompat:1.7.0"
        const val startUp = "androidx.startup:startup-runtime:1.2.0"
        const val dataStore = "androidx.datastore:datastore-preferences:1.1.4"
        const val dataStoreCore = "androidx.datastore:datastore-preferences-core:1.1.4"

    }

    object Lifecycle {
        const val viewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7"
        const val composeViewModel = "androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7"
    }

    object Compose {

        const val ui = "androidx.compose.ui:ui:1.7.8"
        const val material = "androidx.compose.material:material:1.7.8"
        const val tooling = "androidx.compose.ui:ui-tooling:1.7.8"
        const val activity = "androidx.activity:activity-compose:1.10.1"
        const val lifecycle = "androidx.lifecycle:lifecycle-runtime-compose:2.8.7"
        const val icons_core = "androidx.compose.material:material-icons-core:1.7.8"
        const val icons_extended = "androidx.compose.material:material-icons-extended:1.7.8"
    }

    object Kotlin {
        private const val version = "2.1.20"

        const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
        const val parcelizePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
        object Serialization{
            const val serialization = "org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.1"
            const val serializationPlugin = "org.jetbrains.kotlin:kotlin-serialization:$version"
        }

        object Coroutine {
            private const val coroutineVersion = "1.6.1"
            const val coroutineCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutineVersion"
            const val coroutineAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutineVersion"
        }

        const val reflect = "org.jetbrains.kotlin:kotlin-reflect:$version"
    }

    object Dagger {
        private const val daggerVersion = "2.56.1"
        const val android = "com.google.dagger:hilt-android:$daggerVersion"
        const val compiler = "com.google.dagger:hilt-compiler:$daggerVersion"
        const val androidGradlePlugin = "com.google.dagger:hilt-android-gradle-plugin:$daggerVersion"

    }

    object Square {
        const val retrofit = "com.squareup.retrofit2:retrofit:2.9.0"
        const val okhttp3Logging = "com.squareup.okhttp3:logging-interceptor:4.9.1"
        const val serializationConverter = "com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0"
        const val timber = "com.jakewharton.timber:timber:4.7.1"
    }

    object Firebase {
        const val firebase = "com.google.gms:google-services:4.3.15"
        const val bom = "com.google.firebase:firebase-bom:32.1.0"
        const val analytics = "com.google.firebase:firebase-analytics-ktx"
        const val database = "com.google.firebase:firebase-database-ktx"
        const val auth ="com.google.firebase:firebase-auth:22.1.0"
        const val authKtx ="com.google.firebase:firebase-auth-ktx:22.1.0"
        const val authService = "com.google.android.gms:play-services-auth:20.6.0"
    }

    object Test {
        const val junit5 = "org.junit.jupiter:junit-jupiter-api:5.8.1"
        const val Junit5Engine = "org.junit.jupiter:junit-jupiter-engine:5.8.1"
        const val coroutineTest = "org.jetbrains.kotlinx:kotlinx-coroutines-test:1.5.1"
        const val robolectric = "org.robolectric:robolectric:4.8.1"
        const val mockk = "io.mockk:mockk:1.12.5"
        const val hiltTest = "com.google.dagger:hilt-android-testing:2.38.1"
    }

    const val inject = "javax.inject:javax.inject:1"
}
