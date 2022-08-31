/*
 * © 2017-2022 Deviant Studio
 */

package ds.tetris.game

import androidx.compose.ui.graphics.Color

// for debug
internal fun sceneFiller(board: Board) {
    val style = PaintStyle.Fill(Color.Gray)
    with(board) {
        for (i in 0 until AREA_WIDTH - 1) {
            matrix[19, i] = style
            matrix[18, i] = style
            matrix[17, i] = style
            matrix[16, i] = style
            matrix[15, i] = style
            matrix[14, i] = style
            matrix[13, i] = style
            matrix[12, i] = style
        }

    }
}