/*
 * © 2017-2022 Deviant Studio
 */

package ds.tetris.game

import androidx.compose.ui.graphics.Color
//import kotlinx.datetime.Clock

//fun log(s: String) = println("${getDate()}: $s")

//private fun getDate(): String = Clock.System.now().toString()

// for debug
internal fun sceneFiller(board: Board) {
    val style = PaintStyle.Fill(Color.Blue)
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
            matrix[11, i] = style
            matrix[10, i] = style
        }
        matrix[17, 5] = style
        for (r in 0 until matrix.array.size) {
            val row = matrix.array[r]
            for (c in row.indices) {
                val item = row[c]
                if (item != null) {
                    //view.drawBlockAt(c, r, 0xffffffff.toInt(), PaintStyle.FILL)
                }
            }
        }
    }
}