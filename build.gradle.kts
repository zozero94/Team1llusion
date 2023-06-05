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
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.21")
        classpath(Dep.Firebase.firebase)
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }

}// Top-level build file where you can add configuration options common to all sub-projects/modules.

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}