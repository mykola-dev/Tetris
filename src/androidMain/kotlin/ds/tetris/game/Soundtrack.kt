package ds.tetris.game

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import androidx.core.content.getSystemService
import ds.tetris.android.R

class AndroidSoundtrack(context: Context) : Soundtrack() {

    private val attributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_GAME)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .build()

    private var pool = SoundPool.Builder()
        .setAudioAttributes(attributes)
        .setMaxStreams(10)
        .build()

    private val move1 = pool.load(context, R.raw.move1, 1)
    private val move2 = pool.load(context, R.raw.move2, 1)
    private val move3 = pool.load(context, R.raw.move3, 1)
    private val move4 = pool.load(context, R.raw.move4, 1)
    private val wipe1 = pool.load(context, R.raw.wipe1, 1)
    private val wipe2 = pool.load(context, R.raw.wipe2, 1)
    private val wipe3 = pool.load(context, R.raw.wipe3, 1)
    private val wipe4 = pool.load(context, R.raw.wipe4, 1)
    private val start = pool.load(context, R.raw.start, 1)
    private val gameOver = pool.load(context, R.raw.game_over, 1)
    private val rotate = pool.load(context, R.raw.rotate, 1)
    private val levelUp = pool.load(context, R.raw.level_up, 1)

    //val audioManager = context.getSystemService<AudioManager>()

    override fun doPlay(sound: Sound, variant: Int) {
        val clip = when (sound) {
            Sound.MOVE -> when (variant) {
                1 -> move1
                2 -> move2
                3 -> move3
                else -> move4
            }
            Sound.WIPE -> when (variant) {
                1 -> wipe1
                2 -> wipe2
                3 -> wipe3
                4 -> wipe4
                else -> wipe1
            }
            Sound.ROTATE -> rotate
            Sound.START -> start
            Sound.GAME_OVER -> gameOver
            Sound.LEVEL_UP -> levelUp
            else -> error("no sound")
        }
        pool.play(clip, 1f, 1f, 1, 0, 1f)
    }

    fun release() {
        pool.release()
    }

}