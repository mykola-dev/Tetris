/*
 * © 2017-2022 Deviant Studio
 */

package ds.tetris.game

enum class Sound {
    START, GAME_OVER, PAUSE, MOVE, ROTATE, WIPE, LEVEL_UP
}


abstract class Soundtrack {

    var enabled: Boolean = true

    fun play(sound: Sound, variant: Int = 0) {
        if (enabled) doPlay(sound, variant)
    }

    protected abstract fun doPlay(sound: Sound, variant: Int)
}