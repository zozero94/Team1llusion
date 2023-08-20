package team.illusion

import android.content.Context
import android.media.MediaPlayer
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.annotation.RawRes
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlertManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val vibrator = context.getSystemService(Vibrator::class.java)

    fun play(sound: Sound) {
        val player = MediaPlayer.create(context, sound.soundRes)
        player.setOnCompletionListener { player ->
            player.release()
            vibrator.cancel()
        }
        vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        player.start()
    }
}

enum class Sound(@RawRes val soundRes: Int) {
    Error(R.raw.error), Confirm(R.raw.confirm)
}
