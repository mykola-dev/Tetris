/*
 * © 2017 Deviant Studio
 */

package ds.tetris.game

import java.text.SimpleDateFormat
import java.util.Date

val <T> Array<T>.random get() = this[(Math.random() * size).toInt()]
val LongArray.random get() = this[(Math.random() * size).toInt()]

fun log(s: String) = println("${getDate()}: $s")
private fun getDate(): String = SimpleDateFormat("kk:mm:ss.SS").format(Date())

// for debug
fun sceneFiller(board: Board, view: GameView) {
    with(board) {
        for (i in 0 until ds.tetris.game.AREA_WIDTH - 1) {
            area[19, i] = true
            area[18, i] = true
            area[17, i] = true
            area[16, i] = true
            area[15, i] = true
            area[14, i] = true
            area[13, i] = true
            area[12, i] = true
            area[11, i] = true
            area[10, i] = true
        }
        area[17, 5] = false
        for (r in 0 until area.array.size) {
            val row = area.array[r]
            for (c in 0 until row.size) {
                val item = row[c]
                if (item)
                    view.drawBlockAt(c, r, 0xffffffff.toInt(), PaintStyle.FILL)
            }
        }
    }
}