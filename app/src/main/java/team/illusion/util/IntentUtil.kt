package team.illusion.util

import android.content.Context
import android.content.Intent

fun restartIntent(context: Context): Intent {
    val appContext = context.applicationContext
    return appContext.packageManager.getLaunchIntentForPackage(appContext.packageName)
        ?.let {
            Intent.makeRestartActivityTask(it.component)
        } ?: throw IllegalStateException("empty launch intent")
}