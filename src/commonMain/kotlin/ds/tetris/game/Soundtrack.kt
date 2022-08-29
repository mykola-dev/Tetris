/*
 * © 2017-2022 Deviant Studio
 */

package ds.tetris.game

enum class Sound {
    START, GAME_OVER, PAUSE, MOVE, ROTATE, WIPE, LEVEL_UP
}

expect class Soundtrack() {
    fun play(sound: Sound, variant: Int = 0)
}