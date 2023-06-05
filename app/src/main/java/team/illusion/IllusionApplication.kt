package team.illusion

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class IllusionApplication : Application() {
    override fun onCreate() {
        super.onCreate()

    }
}