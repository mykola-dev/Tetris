/*
 * © 2017-2022 Deviant Studio
 */

package ds.tetris.game

import androidx.compose.ui.unit.IntOffset

enum class Direction(val movement: IntOffset) {
    UP(IntOffset(0, -1)),
    DOWN(IntOffset(0, 1)),
    LEFT(IntOffset(-1, 0)),
    RIGHT(IntOffset(1, 0))
}

