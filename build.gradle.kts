buildscript {

    repositories {
        google()
        mavenCentral()
        maven { setUrl("https://jitpack.io") }
    }
    dependencies {
        classpath(Dep.androidGradlePlugin)
        classpath(Dep.Dagger.androidGradlePlugin)
        classpath(Dep.Kotlin.gradlePlugin)
        classpath(Dep.Kotlin.Serialization.serializationPlugin)
        classpath(Dep.Kotlin.parcelizePlugin)
        classpath(Dep.Firebase.firebase)
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }

}
plugins {
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.20" // this version matches your Kotlin version
}
tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
