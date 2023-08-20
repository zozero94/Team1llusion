package team.illusion

import android.content.Context
import android.media.MediaPlayer
import androidx.annotation.RawRes
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SoundManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    fun play(sound: Sound) {
        val player = MediaPlayer.create(context, sound.soundRes)
        player.setOnCompletionListener { it.release() }
        player.start()
    }
}

enum class Sound(@RawRes val soundRes: Int) {
    Error(R.raw.error), Confirm(R.raw.confirm)
}
