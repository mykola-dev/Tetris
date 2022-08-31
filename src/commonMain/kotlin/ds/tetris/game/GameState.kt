/*
 * © 2022 Deviant Studio
 */

package ds.tetris.game

import androidx.compose.ui.unit.IntSize
import ds.tetris.game.figures.Brick

data class GameState(
    val bricks: List<Brick> = emptyList(),
    val next: List<Brick> = emptyList(),
    val score: Int = 0,
    val level: Int = 1,
    val wipedLines: List<Int> = emptyList(),
    val soundEnabled: Boolean = true,
    val state: State = State.PAUSED,
    val areaDimensions: IntSize
) {
    enum class State {
        STARTED, PAUSED, GAME_OVER
    }
}