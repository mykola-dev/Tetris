/*
 * © 2017 Deviant Studio
 */

package ds.tetris.fx

import ds.tetris.game.Sound
import ds.tetris.game.Sound.*
import ds.tetris.game.Soundtrack
import javafx.scene.media.AudioClip
import tornadofx.*

class SoundtrackFx : Soundtrack, Component() {

    private val move1 = AudioClip(resources["/move1.mp3"])
    private val move2 = AudioClip(resources["/move2.mp3"])
    private val move3 = AudioClip(resources["/move3.mp3"])
    private val move4 = AudioClip(resources["/move4.mp3"])
    private val wipe1 = AudioClip(resources["/wipe1.mp3"])
    private val wipe2 = AudioClip(resources["/wipe2.mp3"])
    private val wipe3 = AudioClip(resources["/wipe3.mp3"])
    private val wipe4 = AudioClip(resources["/wipe4.mp3"])
    private val rotate = AudioClip(resources["/rotate.mp3"])
    private val start = AudioClip(resources["/start.mp3"])
    private val levelUp = AudioClip(resources["/level_up.mp3"])
    private val gameOver = AudioClip(resources["/game_over.mp3"])

    override fun play(sound: Sound, variant: Int) {
        val clip = when (sound) {
            MOVE -> when (variant) {
                1 -> move1
                2 -> move2
                3 -> move3
                else -> move4
            }
            WIPE -> when (variant) {
                1 -> wipe1
                2 -> wipe2
                3 -> wipe3
                4 -> wipe4
                else -> wipe1
            }
            ROTATE -> rotate
            START -> start
            GAME_OVER -> gameOver
            LEVEL_UP -> levelUp
            else -> kotlin.error("no sound")
        }
        clip.play()
    }
}